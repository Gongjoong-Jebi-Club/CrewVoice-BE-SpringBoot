package org.skala.crewvoice.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // .env 파일의 모든 환경 변수를 시스템 환경 변수로 설정
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                // 이미 시스템 환경 변수가 설정되어 있지 않은 경우에만 설정
                if (System.getenv(key) == null) {
                    System.setProperty(key, value);
                    log.debug("Loaded env variable: {}", key);
                }
            });

            log.info(".env file loaded successfully");
        } catch (Exception e) {
            log.warn("Failed to load .env file: {}", e.getMessage());
        }
    }
}
