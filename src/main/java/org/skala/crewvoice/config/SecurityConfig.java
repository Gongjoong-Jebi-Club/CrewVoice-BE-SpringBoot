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
                        .requestMatchers("/api/v1/evaluations/callback").permitAll() // 콜백은 CallbackAuthenticationFilter에서 API Key 검증
                        .anyRequest().authenticated() // 토큰 없이 접근 시 403 Forbidden Error 발생
                )
                .addFilterBefore(callbackAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 콜백 필터를 JWT 필터 앞에 배치 (순서상 먼저 등록되면 index N, 그 다음 JWT가 UPAF(N+1) 앞에 등록되면 index N+1이 되어 Callback -> JWT -> UPAF 순서가 됨)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
