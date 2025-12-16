package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * 최종 성적 엔티티
 *
 * <p>각 승무원이 특정 평가를 완료한 후 저장되는 최종 성적 정보입니다.
 */
@Entity
@Table(
    name = "final_grades",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "evaluation_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinalGrade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @Column(name = "step1_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step1Score;

    @Column(name = "step2_kor_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step2KorScore;

    @Column(name = "step2_eng_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step2EngScore;

    @Column(name = "step3_kor_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step3KorScore;

    @Column(name = "step3_eng_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step3EngScore;

    @Column(name = "eng_grade", length = 2, nullable = false)
    private String engGrade;

    @Column(name = "kor_grade", length = 2, nullable = false)
    private String korGrade;

    @Builder
    public FinalGrade(User user, Evaluation evaluation, BigDecimal step1Score) {
        this.user = user;
        this.evaluation = evaluation;
        this.step1Score = step1Score;
        // Step2/Step3 점수는 0으로 초기화
        this.step2KorScore = BigDecimal.ZERO;
        this.step2EngScore = BigDecimal.ZERO;
        this.step3KorScore = BigDecimal.ZERO;
        this.step3EngScore = BigDecimal.ZERO;
        // 등급은 "등급없음"으로 초기화
        this.korGrade = "등급없음";
        this.engGrade = "등급없음";
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * Step2 한국어 점수 업데이트
     */
    public void updateStep2KorScore(BigDecimal score) {
        this.step2KorScore = score.setScale(2, RoundingMode.HALF_UP);
        recalculateKorGrade();
    }

    /**
     * Step2 영어 점수 업데이트
     */
    public void updateStep2EngScore(BigDecimal score) {
        this.step2EngScore = score.setScale(2, RoundingMode.HALF_UP);
        recalculateEngGrade();
    }

    /**
     * Step3 한국어 점수 업데이트
     */
    public void updateStep3KorScore(BigDecimal score) {
        this.step3KorScore = score.setScale(2, RoundingMode.HALF_UP);
        recalculateKorGrade();
    }

    /**
     * Step3 영어 점수 업데이트
     */
    public void updateStep3EngScore(BigDecimal score) {
        this.step3EngScore = score.setScale(2, RoundingMode.HALF_UP);
        recalculateEngGrade();
    }

    /**
     * 한국어 등급 재계산
     * Step2 (1문제) + Step3 (3문제) = 총 4문제의 평균
     * avg >= 80 → A
     * avg >= 70 → B
     * avg < 70 → 등급없음
     */
    private void recalculateKorGrade() {
        // Step2 + Step3 평균 계산 (4문제 평균)
        BigDecimal korAvg = step2KorScore
                .add(step3KorScore)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        if (korAvg.compareTo(BigDecimal.valueOf(80)) >= 0) {
            this.korGrade = "A";
        } else if (korAvg.compareTo(BigDecimal.valueOf(70)) >= 0) {
            this.korGrade = "B";
        } else {
            this.korGrade = "등급없음";
        }
    }

    /**
     * 영어 등급 재계산
     * Step2 (1문제) + Step3 (3문제) = 총 4문제의 평균
     * avg >= 80 → A
     * avg >= 70 → B
     * avg < 70 → 등급없음
     */
    private void recalculateEngGrade() {
        // Step2 + Step3 평균 계산 (4문제 평균)
        BigDecimal engAvg = step2EngScore
                .add(step3EngScore)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        if (engAvg.compareTo(BigDecimal.valueOf(80)) >= 0) {
            this.engGrade = "A";
        } else if (engAvg.compareTo(BigDecimal.valueOf(70)) >= 0) {
            this.engGrade = "B";
        } else {
            this.engGrade = "등급없음";
        }
    }
}