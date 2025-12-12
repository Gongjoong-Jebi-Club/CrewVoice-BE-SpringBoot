package org.skala.crewvoice.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * FastAPI 콜백 요청을 위한 API Key 인증 필터
 * /api/v1/evaluations/callback 경로에 대해 X-API-Key 헤더를 검증합니다.
 */
@Slf4j
@Component
public class CallbackAuthenticationFilter extends OncePerRequestFilter {

    private static final String CALLBACK_PATH = "/api/v1/evaluations/callback";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${callback.api.key}")
    private String callbackApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // 콜백 경로인 경우에만 API Key 검증
        if (CALLBACK_PATH.equals(requestPath)) {
            log.debug("콜백 요청 감지 - API Key 검증 시작: {}", requestPath);

            String apiKey = request.getHeader(API_KEY_HEADER);

            // API Key 검증
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("콜백 요청 거부 - API Key 헤더 없음: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Missing API Key\", \"message\": \"X-API-Key header is required for callback requests\"}");
                return;
            }

            if (!callbackApiKey.equals(apiKey)) {
                log.warn("콜백 요청 거부 - 잘못된 API Key: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid API Key\", \"message\": \"The provided API Key is incorrect\"}");
                return;
            }

            log.info("콜백 요청 인증 성공 - API Key 검증 완료: {}", requestPath);
        }

        // API Key가 유효하거나 콜백 경로가 아닌 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
