package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions_step2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionStep2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionStep2 question;

    @Column(name = "audio_file_korean_url", columnDefinition = "TEXT", nullable = false)
    private String audioFileKoreanUrl;

    @Column(name = "audio_file_english_url", columnDefinition = "TEXT", nullable = false)
    private String audioFileEnglishUrl;

    @Column(name = "audio_duration_korean", precision = 6, scale = 2)
    private BigDecimal audioDurationKorean;

    @Column(name = "audio_duration_english", precision = 6, scale = 2)
    private BigDecimal audioDurationEnglish;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    public SubmissionStep2(User user, Evaluation evaluation, QuestionStep2 question,
                           String audioFileKoreanUrl, String audioFileEnglishUrl,
                           BigDecimal audioDurationKorean, BigDecimal audioDurationEnglish) {
        this.user = user;
        this.evaluation = evaluation;
        this.question = question;
        this.audioFileKoreanUrl = audioFileKoreanUrl;
        this.audioFileEnglishUrl = audioFileEnglishUrl;
        this.audioDurationKorean = audioDurationKorean;
        this.audioDurationEnglish = audioDurationEnglish;
        this.submittedAt = LocalDateTime.now();
    }
}
