package org.skala.crewvoice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 클래스
 *
 * <p>Spring Data JPA의 Auditing 기능을 활성화합니다.
 * Auditing이 활성화되면 {@link org.skala.crewvoice.domain.BaseTimeEntity}를
 * 상속받은 모든 엔티티의 생성일시(createdAt)와 수정일시(updatedAt)가 자동으로 관리됩니다.
 *
 * <p><b>Auditing 기능:</b>
 * <ul>
 *   <li>@CreatedDate - 엔티티 생성 시 자동으로 현재 시간 설정</li>
 *   <li>@LastModifiedDate - 엔티티 수정 시 자동으로 현재 시간 갱신</li>
 *   <li>@CreatedBy - 엔티티 생성자 정보 (설정 필요)</li>
 *   <li>@LastModifiedBy - 엔티티 수정자 정보 (설정 필요)</li>
 * </ul>
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * @Entity
 * public class User extends BaseTimeEntity {
 *     @Id
 *     private Long id;
 *     // createdAt, updatedAt은 자동으로 관리됨
 * }
 * }
 * </pre>
 *
 * <p><b>주의사항:</b>
 * <ul>
 *   <li>이 설정이 활성화되어야 BaseTimeEntity의 @CreatedDate, @LastModifiedDate가 동작합니다</li>
 *   <li>CreatedBy, ModifiedBy를 사용하려면 AuditorAware 빈을 추가로 등록해야 합니다</li>
 *   <li><b>중요:</b> @EnableJpaAuditing은 최소 1개 이상의 @Entity 클래스가 있어야 활성화 가능합니다.
 *       Entity 클래스를 생성한 후 아래 주석을 해제하세요.</li>
 * </ul>
 *
 * @see org.skala.crewvoice.domain.BaseTimeEntity
 * @see org.springframework.data.jpa.repository.config.EnableJpaAuditing
 */
@Configuration
// TODO: Entity 클래스 생성 후 아래 주석을 해제하세요
// @EnableJpaAuditing
public class JpaConfig {
}
