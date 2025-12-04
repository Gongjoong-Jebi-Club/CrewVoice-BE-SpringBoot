package org.skala.crewvoice.domain;

import jakarta.persistence.*;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionStep2 extends BaseTimeEntity {

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

    @Column(name = "audio_file_korean_url", columnDefinition = "TEXT")
    private String audioFileKoreanUrl;

    @Column(name = "audio_file_english_url", columnDefinition = "TEXT")
    private String audioFileEnglishUrl;

    @Column(name = "audio_duration_korean", precision = 6, scale = 2)
    private BigDecimal audioDurationKorean;

    @Column(name = "audio_duration_english", precision = 6, scale = 2)
    private BigDecimal audioDurationEnglish;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "evaluated_at")
    private OffsetDateTime evaluatedAt;
}