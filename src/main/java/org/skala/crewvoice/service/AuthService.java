package org.skala.crewvoice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.skala.crewvoice.dto.request.LoginRequest;
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

        // Redis에 Refresh Token 저장 (클라이언트에는 전송하지 않음)
        redisService.set("RT:" + user.getEmail(), refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);

        return new LoginResponse(accessToken, user.getRole());
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

    public TokenRefreshResponse refreshAccessToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 Access Token 추출
        final String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }
        String accessToken = bearerToken.substring(7);

        // 2. 만료된 Access Token에서 사용자 이메일 추출
        String email = jwtTokenProvider.getSubjectFromExpiredToken(accessToken);

        // 3. Redis에서 해당 사용자의 Refresh Token 조회
        String storedRefreshToken = (String) redisService.get("RT:" + email);
        if (storedRefreshToken == null) {
            throw new IllegalArgumentException("저장된 Refresh Token이 없습니다. 다시 로그인해주세요.");
        }

        // 4. Redis에 저장된 Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(storedRefreshToken)) {
            // Refresh Token도 만료된 경우 Redis에서 삭제
            redisService.delete("RT:" + email);
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        }

        // 5. 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email);

        return new TokenRefreshResponse(newAccessToken);
    }

    /**
     * 테스트용: 만료된 Access Token 생성
     * Redis에 Refresh Token을 저장하여 /refresh API 테스트 가능
     */
    public LoginResponse createExpiredTokenForTest(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 이미 만료된 Access Token 생성
        String expiredAccessToken = jwtTokenProvider.createExpiredAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Redis에 Refresh Token 저장 (테스트를 위해)
        redisService.set("RT:" + user.getEmail(), refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);

        return new LoginResponse(expiredAccessToken, user.getRole());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
