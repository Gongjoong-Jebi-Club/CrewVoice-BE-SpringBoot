package org.skala.crewvoice.dto.response;

import lombok.Getter;

@Getter
public class QuestionStep1Response {
    private final Long questionId;

    private QuestionStep1Response(Long questionId) {
        this.questionId = questionId;
    }

    public static QuestionStep1Response of(Long questionId) {
        return new QuestionStep1Response(questionId);
    }
}
