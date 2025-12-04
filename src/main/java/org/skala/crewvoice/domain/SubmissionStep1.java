package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions_step1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionStep1 {

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
    private QuestionStep1 question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "user_answer", length = 1, nullable = false)
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    public SubmissionStep1(User user, Evaluation evaluation, QuestionStep1 question,
                           Integer questionOrder, String userAnswer, Boolean isCorrect) {
        this.user = user;
        this.evaluation = evaluation;
        this.question = question;
        this.questionOrder = questionOrder;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.submittedAt = LocalDateTime.now();
    }
}
