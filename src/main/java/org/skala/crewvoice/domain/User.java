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
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(name = "is_mock_exam", nullable = false)
    private boolean isMockExam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    public User(String name, String employeeId, String email, String password, String role, String gender, boolean isMockExam, UserStatus status) {
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
}
