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
}