package org.skala.crewvoice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * 모든 Entity의 생성일시와 수정일시를 자동으로 관리하는 추상 클래스
 *
 * <p>JPA Auditing 기능을 사용하여 Entity의 생성/수정 시점을 자동으로 기록합니다.
 * JpaConfig에서 @EnableJpaAuditing이 활성화되어 있어야 합니다.
 *
 * <p><b>사용 방법:</b>
 * <pre>
 * {@code
 * @Entity
 * public class User extends BaseTimeEntity {
 *     @Id
 *     private Long id;
 *     private String name;
 *     // createdAt, updatedAt은 자동으로 관리됨
 * }
 * }
 * </pre>
 *
 * <p><b>주의사항:</b>
 * <ul>
 *   <li>createdAt은 Entity 생성 시 한 번만 설정되며, 이후 수정 불가</li>
 *   <li>updatedAt은 Entity가 수정될 때마다 자동으로 갱신됨</li>
 *   <li>수동으로 시간을 설정할 필요 없음 (자동 관리)</li>
 * </ul>
 *
 * @see org.skala.crewvoice.config.JpaConfig
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /**
     * 엔티티 생성 일시 (UTC)
     * Entity가 처음 저장될 때 자동으로 현재 시간이 설정됩니다.
     * timestamptz 타입과 호환됩니다.
     */
    @CreatedDate
    @Column(updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    /**
     * 엔티티 최종 수정 일시 (UTC)
     * Entity가 수정될 때마다 자동으로 현재 시간으로 갱신됩니다.
     * timestamptz 타입과 호환됩니다.
     */
    @LastModifiedDate
    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}
