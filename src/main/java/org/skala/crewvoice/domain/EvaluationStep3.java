package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations_step3",
        indexes = @Index(name = "idx_step3_task_id", columnList = "task_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EvaluationStep3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_step3_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private SubmissionStep3 submission;

    @Column(name = "task_id", unique = true, nullable = false)
    private String taskId;

    // LanguageConverter를 통해 DB에 "ko-KR" 또는 "en-US"로 저장됨
    @Column(name = "language", length = 10, nullable = false)
    private Language language;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // PROCESSING, COMPLETED, FAILED

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // 점수 필드들은 nullable (FastAPI callback에서 채워짐)
    @Column(name = "score_pronunciation", precision = 5, scale = 2)
    private BigDecimal scorePronunciation;

    @Column(name = "score_pause", precision = 5, scale = 2)
    private BigDecimal scorePause;

    @Column(name = "score_intonation", precision = 5, scale = 2)
    private BigDecimal scoreIntonation;

    @Column(name = "score_tone", precision = 5, scale = 2)
    private BigDecimal scoreTone;

    @Column(name = "score_speed", precision = 5, scale = 2)
    private BigDecimal scoreSpeed;

    @Column(name = "score_accent", precision = 5, scale = 2)
    private BigDecimal scoreAccent;

    @Column(name = "overall_score", precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "feedback_summary", columnDefinition = "TEXT")
    private String feedbackSummary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 평가 점수 업데이트 (FastAPI callback에서 호출)
     */
    public void updateScores(BigDecimal scorePronunciation, BigDecimal scorePause,
                             BigDecimal scoreIntonation, BigDecimal scoreTone,
                             BigDecimal scoreSpeed, BigDecimal scoreAccent,
                             BigDecimal overallScore, String feedbackSummary) {
        this.scorePronunciation = scorePronunciation;
        this.scorePause = scorePause;
        this.scoreIntonation = scoreIntonation;
        this.scoreTone = scoreTone;
        this.scoreSpeed = scoreSpeed;
        this.scoreAccent = scoreAccent;
        this.overallScore = overallScore;
        this.feedbackSummary = feedbackSummary;
    }

    /**
     * 평가 상태 업데이트
     */
    public void updateStatus(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * 평가 완료 상태로 변경
     */
    public void markAsCompleted() {
        this.status = "COMPLETED";
    }

    /**
     * 평가 실패 상태로 변경
     */
    public void markAsFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
    }
}
