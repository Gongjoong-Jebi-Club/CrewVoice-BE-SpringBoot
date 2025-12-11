package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.status = :status " +
           "AND (:keyword IS NULL OR :keyword = '' OR u.name LIKE %:keyword% OR u.employeeId LIKE %:keyword% OR u.email LIKE %:keyword%) " +
           "AND (:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") org.skala.crewvoice.domain.Role role, @Param("status") UserStatus status);

    /**
     * ACTIVE 상태의 CREW role 사용자 수 조회 (평가 대상 인원)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = org.skala.crewvoice.domain.Role.CREW")
    Integer countActiveCrewMembers();

    /**
     * ACTIVE 상태의 모든 CREW role 사용자 조회 (이름순 정렬)
     * 평가 대상자 목록 조회에 사용
     */
    @Query("SELECT u FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = org.skala.crewvoice.domain.Role.CREW ORDER BY u.name ASC")
    List<User> findAllActiveCrewMembers();

    /**
     * CREW 역할을 가진 모든 사용자를 조회합니다.
     *
     * @return CREW 역할 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = org.skala.crewvoice.domain.Role.CREW ORDER BY u.createdAt DESC")
    List<User> findAllCrews();

    /**
     * CREW 역할을 가진 ACTIVE 사용자를 키워드로 검색합니다.
     * 이름 또는 사번에 키워드가 포함된 사용자를 조회합니다.
     * 대소문자 구분 없이 검색합니다 (사번에 영어+숫자 혼합 가능).
     *
     * @param keyword 검색 키워드 (이름 또는 사번, 이메일 제외)
     * @return 검색된 CREW 사용자 목록 (생성일 내림차순)
     */
    @Query("SELECT u FROM User u " +
           "WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE " +
           "AND u.role = org.skala.crewvoice.domain.Role.CREW " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY u.createdAt DESC")
    List<User> searchCrewsByKeyword(@Param("keyword") String keyword);

    /**
     * 여러 ID에 해당하는 사용자들을 한 번의 쿼리로 조회합니다.
     * N+1 문제를 해결하기 위해 사용합니다.
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return 조회된 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findAllByIds(@Param("userIds") List<Long> userIds);
}
