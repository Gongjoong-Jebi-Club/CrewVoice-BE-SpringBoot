package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep2;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep2DetailResponse {
    private Long questionId;
    private String aircraftType;
    private String situationDescription;
    private String scriptKorean;
    private String scriptEnglish;
    private String explanation;

    public static QuestionStep2DetailResponse from(QuestionStep2 question) {
        return new QuestionStep2DetailResponse(
                question.getQuestionId(),
                question.getAircraftType(),
                question.getSituationDescription(),
                question.getScriptKorean(),
                question.getScriptEnglish(),
                question.getExplanation()
        );
    }
}
