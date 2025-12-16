package org.skala.crewvoice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * JPA 설정 클래스
 *
 * <p>Spring Data JPA의 Auditing 기능을 활성화하고,
 * OffsetDateTime 타입을 지원하도록 설정합니다.
 * 모든 시간은 한국 표준시(KST, Asia/Seoul)를 기준으로 합니다.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {

    /**
     * JPA Auditing 기능이 OffsetDateTime 타입을 사용하여
     * 현재 시간을 기록하도록 설정하는 DateTimeProvider 빈을 등록합니다.
     * 한국 표준시(KST)를 기준으로 현재 시간을 반환합니다.
     *
     * @return 현재 시간을 KST 기준 OffsetDateTime으로 반환하는 DateTimeProvider
     */
    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}
