package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep2;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep2RandomResponse {
    private Long questionId;
    private String situationDescription;
    private String aircraftType;

    public static QuestionStep2RandomResponse from(QuestionStep2 question) {
        return new QuestionStep2RandomResponse(
                question.getQuestionId(),
                question.getSituationDescription(),
                question.getAircraftType()
        );
    }
}
