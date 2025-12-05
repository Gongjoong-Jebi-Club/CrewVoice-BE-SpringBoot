package org.skala.crewvoice.dto.response;

import org.skala.crewvoice.domain.QuestionStep1;

public class QuestionStep1DetailResponse {
    private final Long questionId;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String answer;
    private final String explanation;

    private QuestionStep1DetailResponse(Long questionId, String questionText, String optionA,
                                        String optionB, String optionC, String optionD,
                                        String answer, String explanation) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.explanation = explanation;
    }

    public static QuestionStep1DetailResponse from(QuestionStep1 question) {
        return new QuestionStep1DetailResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(),
                question.getExplanation()
        );
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
