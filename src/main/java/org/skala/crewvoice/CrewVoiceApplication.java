package org.skala.crewvoice;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.skala.crewvoice.repository")
public class CrewVoiceApplication {

    @PostConstruct
    public void init() {
        // 애플리케이션 전체 타임존을 한국 표준시(KST)로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        // Windows 호환성: Netty 네이티브 전송(epoll) 비활성화
        System.setProperty("reactor.netty.native", "false");

        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            if (entry.getKey().equals("DB_HOST") || entry.getKey().equals("DB_NAME") ||
                entry.getKey().equals("DB_USERNAME") || entry.getKey().equals("DB_PASSWORD")) {
                System.out.println("Loaded env: " + entry.getKey() + " = " +
                    (entry.getKey().contains("PASSWORD") ? "****" : entry.getValue()));
            }
        });

        SpringApplication.run(CrewVoiceApplication.class, args);
    }

}
