package org.skala.crewvoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.skala.crewvoice.repository")
public class CrewVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewVoiceApplication.class, args);
    }

}
