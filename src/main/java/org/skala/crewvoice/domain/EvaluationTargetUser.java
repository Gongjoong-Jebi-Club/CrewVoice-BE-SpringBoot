package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 평가 대상자 엔티티
 *
 * <p>evaluations와 users의 다대다 관계를 표현하는 중간 테이블.
 * 각 평가(evaluation)에 대한 평가 대상자(user) 목록을 저장.
 */
@Entity
@Table(name = "evaluation_target_users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluationTargetUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 사용자가 해당 평가를 응시했는지 여부
     * false: 미응시, true: 응시 완료
     */
    @Column(name = "is_taken", nullable = false)
    private Boolean isTaken = false;

    @Builder
    public EvaluationTargetUser(Evaluation evaluation, User user) {
        this.evaluation = evaluation;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.isTaken = false;  // 배정 시점에는 미응시 상태
    }

    /**
     * 사용자가 평가를 응시했음을 표시
     */
    public void markAsTaken() {
        this.isTaken = true;
    }
}
