package org.skala.crewvoice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 기본 설정 클래스
 *
 * <p>Spring Security의 기본적인 보안 설정을 제공합니다.
 *
 * <p><b>현재 설정:</b>
 * <ul>
 *   <li>CSRF 비활성화 (REST API)</li>
 *   <li>모든 요청 허용 (개발 단계)</li>
 *   <li>PasswordEncoder 제공 (BCrypt)</li>
 * </ul>
 *
 * <p><b>JWT 인증 추가 시:</b>
 * <ul>
 *   <li>JWT 관련 필터 추가</li>
 *   <li>세션 정책 변경 (STATELESS)</li>
 *   <li>URL별 권한 설정</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 모든 요청 허용 (개발 단계 - 나중에 수정 필요)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
