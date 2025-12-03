package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep3;
import org.skala.crewvoice.util.DateFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep3ListResponse {
    private Long questionId;
    private String situationDescription;
    private String aircraftType;
    private String arrival;
    private String departure;
    private String boardingDate;
    private String explanation;

    public static QuestionStep3ListResponse from(QuestionStep3 question) {
        return new QuestionStep3ListResponse(
                question.getId(),
                question.getSituationDescription(),
                question.getAircraftType(),
                question.getArrival(),
                question.getDeparture(),
                DateFormatter.formatToMMdd(question.getBoardingDate()),
                question.getExplanation()
        );
    }
}
