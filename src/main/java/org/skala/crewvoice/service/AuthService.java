package org.skala.crewvoice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.skala.crewvoice.domain.Gender;
import org.skala.crewvoice.domain.Role;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.skala.crewvoice.dto.request.LoginRequest;
import org.skala.crewvoice.dto.request.SignUpRequest;
import org.skala.crewvoice.dto.response.LoginResponse;
import org.skala.crewvoice.dto.response.SignUpResponse;
import org.skala.crewvoice.dto.response.TokenRefreshResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.DuplicateEntityException;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidCredentialsException;
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
            throw new DuplicateEntityException(ErrorCode.DUPLICATE_EMAIL);
        }

        // Role과 Gender를 Enum으로 변환
        Role role = "ADMIN".equalsIgnoreCase(signUpRequest.getRole()) ? Role.ADMIN : Role.CREW;
        Gender gender = Gender.fromString(signUpRequest.getGender());

        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmployeeId(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                role,
                gender,
                false, // is_mock_exam 기본값: false
                UserStatus.ACTIVE // status 기본값: ACTIVE
        );
        User savedUser = userRepository.save(user);
        return new SignUpResponse(savedUser.getId());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Redis에 Refresh Token 저장 (클라이언트에는 전송하지 않음)
        redisService.set("RT:" + user.getEmail(), refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);

        return new LoginResponse(accessToken, user.getRole().name());
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
            throw new InvalidCredentialsException(ErrorCode.INVALID_AUTH_HEADER);
        }
        String accessToken = bearerToken.substring(7);

        // 2. 만료된 Access Token에서 사용자 이메일 추출
        String email = jwtTokenProvider.getSubjectFromExpiredToken(accessToken);

        // 3. Redis에서 해당 사용자의 Refresh Token 조회
        String storedRefreshToken = (String) redisService.get("RT:" + email);
        if (storedRefreshToken == null) {
            throw new InvalidCredentialsException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 4. Redis에 저장된 Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(storedRefreshToken)) {
            // Refresh Token도 만료된 경우 Redis에서 삭제
            redisService.delete("RT:" + email);
            throw new InvalidCredentialsException(ErrorCode.REFRESH_TOKEN_EXPIRED);
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(ErrorCode.INVALID_PASSWORD);
        }

        // 이미 만료된 Access Token 생성
        String expiredAccessToken = jwtTokenProvider.createExpiredAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Redis에 Refresh Token 저장 (테스트를 위해)
        redisService.set("RT:" + user.getEmail(), refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);

        return new LoginResponse(expiredAccessToken, user.getRole().name());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
