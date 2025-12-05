package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions_step1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionStep1 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "option_a", length = 500, nullable = false)
    private String optionA;

    @Column(name = "option_b", length = 500, nullable = false)
    private String optionB;

    @Column(name = "option_c", length = 500, nullable = false)
    private String optionC;

    @Column(name = "option_d", length = 500, nullable = false)
    private String optionD;

    @Column(name = "correct_answer", length = 1, nullable = false)
    private String correctAnswer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_used", length = 10, nullable = false)
    private String isUsed;

    @Column(name = "status", length = 10, nullable = false) //추가: 문제 상태 (ACTIVE, DELETED)
    private String status;

     // Soft Delete 처리
    public void delete() {
        this.status = "INACTIVE";
    }

    // 활성화 처리
    public void activate() {
        this.status = "ACTIVE";
    }

    public QuestionStep1(String questionText, String optionA, String optionB,
                         String optionC, String optionD, String correctAnswer,
                         String explanation) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.isUsed = "MOCK";
        this.status = "ACTIVE";
    }
}
