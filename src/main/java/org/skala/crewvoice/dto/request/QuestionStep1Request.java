package org.skala.crewvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QuestionStep1Request {

    private Long questionId; // nullable: null이면 추가, 값이 있으면 수정

    @NotBlank(message = "문제 내용은 필수입니다.")
    private String questionText;

    @NotBlank(message = "선택지 A는 필수입니다.")
    private String optionA;

    @NotBlank(message = "선택지 B는 필수입니다.")
    private String optionB;

    @NotBlank(message = "선택지 C는 필수입니다.")
    private String optionC;

    @NotBlank(message = "선택지 D는 필수입니다.")
    private String optionD;

    @NotBlank(message = "정답은 필수입니다.")
    @Pattern(regexp = "^[A-Da-d]$", message = "정답은 A, B, C, D 중 하나여야 합니다.")
    private String answer;

    private String explanation; // nullable

    public QuestionStep1Request() {
    }

    public QuestionStep1Request(Long questionId, String questionText, String optionA, String optionB,
                                String optionC, String optionD, String answer, String explanation) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.explanation = explanation;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getAnswer() {
        return answer;
    }

    public String getExplanation() {
        return explanation;
    }
}
