package org.skala.crewvoice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.skala.crewvoice.dto.request.LoginRequest;
import org.skala.crewvoice.dto.request.RefreshTokenRequest;
import org.skala.crewvoice.dto.request.SignUpRequest;
import org.skala.crewvoice.dto.response.LoginResponse;
import org.skala.crewvoice.dto.response.SignUpResponse;
import org.skala.crewvoice.dto.response.TokenRefreshResponse;
import org.skala.crewvoice.repository.UserRepository;
import org.skala.crewvoice.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final long refreshTokenValidityInMilliseconds;

    // 순환 참조를 해결하기 위해 PasswordEncoder에 @Lazy 어노테이션 추가
    public AuthService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RedisService redisService,
                       @Value("${security.jwt.token.refresh-expire-length}") long refreshTokenValidityInMilliseconds) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmployeeId(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRole(),
                signUpRequest.getGender(),
                false, // is_mock_exam 기본값: false
                UserStatus.ACTIVE // status 기본값: ACTIVE
        );
        User savedUser = userRepository.save(user);
        return new SignUpResponse(savedUser.getId());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Redis에 Refresh Token 저장
        redisService.set("RT:" + user.getEmail(), refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);

        return new LoginResponse(accessToken, refreshToken, user.getRole());
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(accessToken)) {
                Long remainingTime = jwtTokenProvider.getRemainingTime(accessToken);
                // Access Token을 블랙리스트에 추가
                redisService.set("blacklist:" + accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS);
                // 해당 사용자의 Refresh Token도 삭제
                String email = jwtTokenProvider.getSubject(accessToken);
                redisService.delete("RT:" + email);
            }
        }
    }

    public TokenRefreshResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        // 2. 토큰에서 사용자 이메일 추출
        String email = jwtTokenProvider.getSubject(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken = (String) redisService.get("RT:" + email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new IllegalArgumentException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email);

        return new TokenRefreshResponse(newAccessToken);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
