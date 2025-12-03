package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep3;
import org.skala.crewvoice.util.DateFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep3DetailResponse {
    private String aircraftType;
    private String departure;
    private String arrival;
    private String boardingDate;
    private String situationDescription;
    private String explanation;

    public static QuestionStep3DetailResponse from(QuestionStep3 question) {
        return new QuestionStep3DetailResponse(
                question.getAircraftType(),
                question.getDeparture(),
                question.getArrival(),
                DateFormatter.formatToMMdd(question.getBoardingDate()),
                question.getSituationDescription(),
                question.getExplanation()
        );
    }
}
