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
 * STEP2 제출 내역 Entity
 */
@Entity
@Table(name = "submissions_step2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SubmissionStep2 {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionStep2 question;

    // LanguageConverter를 통해 DB에 "ko-KR" 또는 "en-US"로 저장됨
    @Column(name = "language", length = 10, nullable = false)
    private Language language;

    @Column(name = "audio_file_url", columnDefinition = "TEXT", nullable = false)
    private String audioFileUrl;

    @Column(name = "audio_duration", precision = 6, scale = 2)
    private BigDecimal audioDuration;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "evaluated_at")
    private OffsetDateTime evaluatedAt;
}