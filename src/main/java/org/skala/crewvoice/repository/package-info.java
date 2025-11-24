/**
 * JPA Repository 인터페이스 패키지
 *
 * <p>데이터베이스 접근을 담당하는 Repository 인터페이스들을 포함합니다.
 * Spring Data JPA를 사용하여 기본 CRUD 및 쿼리 메서드를 자동으로 제공받습니다.
 *
 * <p><b>Repository 작성 규칙:</b>
 * <ul>
 *   <li>JpaRepository&lt;Entity, ID&gt;를 상속받아야 합니다</li>
 *   <li>인터페이스로 작성하며, 구현체는 Spring Data JPA가 자동 생성</li>
 *   <li>메서드명 기반 쿼리 또는 @Query 어노테이션 활용</li>
 *   <li>복잡한 조회 로직은 QueryDSL 또는 @Query 사용 권장</li>
 * </ul>
 *
 * <p><b>Repository 예시:</b>
 * <pre>
 * {@code
 * public interface UserRepository extends JpaRepository<User, Long> {
 *
 *     // 메서드명 기반 쿼리
 *     Optional<User> findByEmail(String email);
 *     List<User> findByNameContaining(String name);
 *     boolean existsByEmail(String email);
 *
 *     // @Query 어노테이션 활용
 *     @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate")
 *     List<User> findRecentUsers(@Param("startDate") LocalDateTime startDate);
 *
 *     // 페이징 지원
 *     Page<User> findByNameContaining(String name, Pageable pageable);
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>UserRepository.java - 사용자 Repository</li>
 *   <li>EvaluationRepository.java - 평가 Repository</li>
 *   <li>ScriptRepository.java - 스크립트 Repository</li>
 *   <li>EvaluationDetailRepository.java - 평가 상세 Repository</li>
 * </ul>
 *
 * @see org.skala.crewvoice.domain
 */
package org.skala.crewvoice.repository;
