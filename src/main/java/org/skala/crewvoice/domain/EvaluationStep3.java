package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations_step3")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluationStep3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_step3_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private SubmissionStep3 submission;

    @Column(name = "language", length = 10, nullable = false)
    private String language;

    @Column(name = "score_pronunciation", precision = 5, scale = 2, nullable = false)
    private BigDecimal scorePronunciation;

    @Column(name = "score_pause", precision = 5, scale = 2, nullable = false)
    private BigDecimal scorePause;

    @Column(name = "score_intonation", precision = 5, scale = 2, nullable = false)
    private BigDecimal scoreIntonation;

    @Column(name = "score_tone", precision = 5, scale = 2, nullable = false)
    private BigDecimal scoreTone;

    @Column(name = "score_speed", precision = 5, scale = 2, nullable = false)
    private BigDecimal scoreSpeed;

    @Column(name = "score_accent", precision = 5, scale = 2, nullable = false)
    private BigDecimal scoreAccent;

    @Column(name = "overall_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal overallScore;

    @Column(name = "feedback_summary", columnDefinition = "TEXT")
    private String feedbackSummary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public EvaluationStep3(SubmissionStep3 submission, String language,
                           BigDecimal scorePronunciation, BigDecimal scorePause,
                           BigDecimal scoreIntonation, BigDecimal scoreTone,
                           BigDecimal scoreSpeed, BigDecimal scoreAccent,
                           BigDecimal overallScore, String feedbackSummary) {
        this.submission = submission;
        this.language = language;
        this.scorePronunciation = scorePronunciation;
        this.scorePause = scorePause;
        this.scoreIntonation = scoreIntonation;
        this.scoreTone = scoreTone;
        this.scoreSpeed = scoreSpeed;
        this.scoreAccent = scoreAccent;
        this.overallScore = overallScore;
        this.feedbackSummary = feedbackSummary;
        this.createdAt = LocalDateTime.now();
    }
}
