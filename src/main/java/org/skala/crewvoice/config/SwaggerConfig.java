package org.skala.crewvoice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger (OpenAPI) 설정 클래스.
 * <p>
 * 이 클래스는 SpringDoc OpenAPI 라이브러리를 사용하여 API 문서를 생성하고 설정합니다.
 * 주요 설정 내용:
 * <ul>
 *     <li><b>OpenAPI Info:</b> API 제목, 설명, 버전, 연락처, 라이선스 정보 설정</li>
 *     <li><b>Servers:</b> 운영(Production) 및 로컬(Local) 서버 URL 설정</li>
 *     <li><b>Security:</b> JWT 인증을 위한 'Bearer Authentication' 스키마 설정</li>
 * </ul>
 * <p>
 * 접속 주소:
 * <ul>
 *     <li>UI: /swagger-ui/index.html</li>
 *     <li>JSON: /v3/api-docs</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI crewVoiceOpenAPI() {
        Server prodServer = new Server();
        prodServer.setUrl("https://server.crewvoice.cloud");
        prodServer.setDescription("Production Server");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("CrewVoice Team");
        contact.setEmail("contact@crewvoice.com");

        Info info = new Info()
                .title("CrewVoice API Documentation")
                .version("2.0.0")
                .description("항공사 객실승무원 기내방송 AI 평가 시스템 REST API")
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"));

        // Swagger 헤더에 Bearer JWT 인증 활성화
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(info)
                .servers(List.of(prodServer, localServer))
                .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}