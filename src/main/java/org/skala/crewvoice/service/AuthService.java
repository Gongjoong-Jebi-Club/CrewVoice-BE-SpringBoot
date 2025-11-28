package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.skala.crewvoice.dto.request.LoginRequest;
import org.skala.crewvoice.dto.request.SignUpRequest;
import org.skala.crewvoice.dto.response.LoginResponse;
import org.skala.crewvoice.dto.response.SignUpResponse;
import org.skala.crewvoice.repository.UserRepository;
import org.skala.crewvoice.util.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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

        String token = jwtTokenProvider.createToken(user.getEmail());
        return new LoginResponse(token);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
