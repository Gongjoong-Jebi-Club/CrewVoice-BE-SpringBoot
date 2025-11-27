package org.skala.crewvoice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.skala.crewvoice.repository")
public class CrewVoiceApplication {

    public static void main(String[] args) {
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
