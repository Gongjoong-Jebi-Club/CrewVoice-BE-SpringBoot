/**
 * JPA Entity 클래스 패키지
 *
 * <p>데이터베이스 테이블과 매핑되는 Entity 클래스들을 포함합니다.
 *
 * <p><b>Entity 작성 규칙:</b>
 * <ul>
 *   <li>모든 Entity는 {@link org.skala.crewvoice.domain.BaseTimeEntity}를 상속받아야 합니다</li>
 *   <li>@Entity 어노테이션 필수</li>
 *   <li>@Table 어노테이션으로 테이블명 명시 (선택)</li>
 *   <li>Lombok의 @Getter, @Builder 등 활용 (단, @Data는 지양)</li>
 *   <li>연관관계 매핑 시 FetchType.LAZY를 기본으로 사용</li>
 * </ul>
 *
 * <p><b>Entity 예시:</b>
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "users")
 * @Getter
 * @NoArgsConstructor(access = AccessLevel.PROTECTED)
 * public class User extends BaseTimeEntity {
 *
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *
 *     @Column(nullable = false, length = 50)
 *     private String name;
 *
 *     @Column(nullable = false, unique = true)
 *     private String email;
 *
 *     @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
 *     private List<Evaluation> evaluations = new ArrayList<>();
 *
 *     @Builder
 *     public User(String name, String email) {
 *         this.name = name;
 *         this.email = email;
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>User.java - 사용자 엔티티</li>
 *   <li>Evaluation.java - 평가 엔티티</li>
 *   <li>Script.java - 스크립트 엔티티</li>
 *   <li>EvaluationDetail.java - 평가 상세 엔티티</li>
 *   <li>기타 도메인 모델에 필요한 엔티티들</li>
 * </ul>
 *
 * @see org.skala.crewvoice.domain.BaseTimeEntity
 * @see org.skala.crewvoice.repository
 */
package org.skala.crewvoice.domain;
