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
           "AND (:role IS NULL OR :role = '' OR u.role = :role)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") String role, @Param("status") UserStatus status);

    /**
     * ACTIVE 상태의 CREW role 사용자 수 조회 (평가 대상 인원)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = 'CREW'")
    Integer countActiveCrewMembers();

    /**
     * ACTIVE 상태의 모든 CREW role 사용자 조회 (이름순 정렬)
     * 평가 대상자 목록 조회에 사용
     */
    @Query("SELECT u FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = 'CREW' ORDER BY u.name ASC")
    List<User> findAllActiveCrewMembers();

    /**
     * CREW 역할을 가진 모든 사용자를 조회합니다.
     *
     * @return CREW 역할 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.status = org.skala.crewvoice.domain.UserStatus.ACTIVE AND u.role = 'CREW' ORDER BY u.createdAt DESC")
    List<User> findAllCrews();
}
