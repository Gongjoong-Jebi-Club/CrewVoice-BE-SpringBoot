package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.Question;
import org.skala.crewvoice.domain.QuestionStep2;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private String questionId;
    private String situationDescription;
    private String aircraftType;
    private String explaination;

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                String.valueOf(question.getId()),
                question.getSituationDescription(),
                question.getAircraftType(),
                question.getExplaination()
        );
    }

    public static QuestionResponse from(QuestionStep2 question) {
        return new QuestionResponse(
                String.valueOf(question.getId()),
                question.getSituationDescription(),
                question.getAircraftType(),
                question.getExplanation()
        );
    }
}
