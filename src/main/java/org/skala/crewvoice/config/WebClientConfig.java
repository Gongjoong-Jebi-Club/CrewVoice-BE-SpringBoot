package org.skala.crewvoice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 설정
 * FastAPI 서버와의 비동기 통신을 위한 WebClient 빈 설정
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${fastapi.base-url}")
    private String fastapiBaseUrl;

    @Value("${fastapi.timeout.connection}")
    private int connectionTimeout;

    @Value("${fastapi.timeout.response}")
    private int responseTimeout;

    /**
     * FastAPI 서버 통신용 WebClient 빈 생성
     * - Connection Timeout: 5초 (환경변수 설정 가능)
     * - Response Timeout: 10초 (환경변수 설정 가능)
     * - Max In-Memory Size: 16MB (오디오 파일 URL 전송용)
     *
     * @return WebClient 인스턴스
     */
    @Bean
    public WebClient fastapiWebClient() {
        // HttpClient 설정 (Netty 기반, NIO 강제 사용)
        HttpClient httpClient = HttpClient.create()
                // Connection Timeout 설정
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                // Response Timeout 설정
                .responseTimeout(Duration.ofMillis(responseTimeout))
                // Read/Write Timeout 설정
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
                )
                // Windows 호환성을 위해 NIO 강제 사용 (epoll 비활성화)
                .runOn(reactor.netty.resources.LoopResources.create("fastapi-http"));

        return WebClient.builder()
                .baseUrl(fastapiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // 요청/응답 로깅 필터 추가
                .filter(logRequest())
                .filter(logResponse())
                // 16MB 버퍼 크기 설정 (대용량 데이터 처리용)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    /**
     * 요청 로깅 필터
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("FastAPI 요청 전송 - Method: {}, URI: {}",
                    clientRequest.method(),
                    clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    /**
     * 응답 로깅 필터
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("FastAPI 응답 수신 - Status Code: {}",
                    clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
