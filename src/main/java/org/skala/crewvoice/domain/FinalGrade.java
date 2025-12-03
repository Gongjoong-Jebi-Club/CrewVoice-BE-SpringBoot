package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "final_grades")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinalGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @Column(name = "step1_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step1Score;

    @Column(name = "step2_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step2Score;

    @Column(name = "step3_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal step3Score;

    @Column(name = "total_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal totalScore;

    @Column(name = "eng_grade", length = 2, nullable = false)
    private String engGrade;

    @Column(name = "kor_grade", length = 2, nullable = false)
    private String korGrade;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public FinalGrade(User user, Evaluation evaluation, BigDecimal step1Score, BigDecimal step2Score,
                      BigDecimal step3Score, BigDecimal totalScore, String engGrade, String korGrade) {
        this.user = user;
        this.evaluation = evaluation;
        this.step1Score = step1Score;
        this.step2Score = step2Score;
        this.step3Score = step3Score;
        this.totalScore = totalScore;
        this.engGrade = engGrade;
        this.korGrade = korGrade;
        this.createdAt = LocalDateTime.now();
    }
}
