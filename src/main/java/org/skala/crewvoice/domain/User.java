package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Gender gender;

    @Column(name = "is_mock_exam", nullable = false)
    private boolean isMockExam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    public User(String name, String employeeId, String email, String password, Role role, Gender gender, boolean isMockExam, UserStatus status) {
        this.name = name;
        this.employeeId = employeeId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.gender = gender;
        this.isMockExam = isMockExam;
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 사용자의 상태가 ACTIVE일 때만 계정이 활성화되도록 수정
        return this.status == UserStatus.ACTIVE;
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 사용자가 관리자 역할인지 확인
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    /**
     * 사용자가 CREW 역할인지 확인
     */
    public boolean isCrew() {
        return this.role == Role.CREW;
    }

    /**
     * 사용자가 활성 상태인지 확인
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * 사용자가 활성 상태의 CREW 인지 확인
     */
    public boolean isActiveCrew() {
        return isActive() && isCrew();
    }

    /**
     * 사용자가 평가에 참여할 수 있는지 확인
     *
     * @param evaluation 참여하려는 평가
     * @return 참여 가능 여부
     */
    public boolean canTakeEvaluation(Evaluation evaluation) {
        if (!isActiveCrew()) {
            return false;
        }

        // 모의 평가 참여 권한 확인
        if (evaluation.getEvaluationType() == EvaluationType.MOCK && !this.isMockExam) {
            return false;
        }

        return true;
    }

    /**
     * 사용자 프로필 업데이트
     *
     * @param name 이름
     * @param employeeId 사번
     */
    public void updateProfile(String name, String employeeId) {
        validateName(name);
        validateEmployeeId(employeeId);
        this.name = name;
        this.employeeId = employeeId;
    }

    /**
     * 사용자 계정 비활성화
     */
    public void delete() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 사용자 계정 활성화
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 비밀번호 재설정
     *
     * @param encodedPassword 암호화된 새 비밀번호
     */
    public void resetPassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = encodedPassword;
    }

    // ===== Validation Methods =====

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
    }
}
