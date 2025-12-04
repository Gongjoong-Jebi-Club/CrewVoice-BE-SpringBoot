package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 최종 성적 엔티티
 *
 * <p>각 승무원이 특정 평가를 완료한 후 저장되는 최종 성적 정보입니다.
 */
@Entity
@Table(name = "final_grades")
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
}