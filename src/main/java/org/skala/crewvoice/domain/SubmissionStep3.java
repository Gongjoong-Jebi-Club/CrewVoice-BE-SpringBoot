package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * STEP3 제출 내역 Entity
 */
@Entity
@Table(name = "submissions_step3")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SubmissionStep3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "part_order", nullable = false)
    private Integer partOrder;  // 1, 2, 3

    @Column(name = "language", length = 10, nullable = false)
    private String language;  // "ko-KR", "en-US", "zh-CN"

    @Column(name = "audio_file_url", columnDefinition = "TEXT", nullable = false)
    private String audioFileUrl;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "evaluated_at")
    private OffsetDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = OffsetDateTime.now();
    }
}