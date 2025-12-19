package org.skala.crewvoice.config;

import org.skala.crewvoice.util.CallbackAuthenticationFilter;
import org.skala.crewvoice.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CallbackAuthenticationFilter callbackAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http)) // CORS 설정 활성화
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/login", "/api/v1/sign-up", "/api/v1/refresh", "/api/v1/test/**", "/swagger-ui/**", "/v3/api-docs/**", "/health").permitAll()
                        .requestMatchers("/api/v1/evaluations/callback").permitAll() // CallbackAuthenticationFilter에서 별도의 API Key로 인증 처리
                        .anyRequest().authenticated() // 그 외 모든 요청은 JWT 인증 필요
                )
                // Filter 순서: CallbackAuthenticationFilter -> JwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter
                // 1. CallbackFilter: 외부 AI 서비스의 콜백 요청(API Key)을 처리. 해당 경로가 아니면 다음 필터로 통과.
                // 2. JwtFilter: 일반 사용자의 Access Token을 검증.
                .addFilterBefore(callbackAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
