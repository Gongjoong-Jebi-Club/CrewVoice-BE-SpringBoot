package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep3;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep3RandomResponse {
    private List<Long> questionIdList;
    private String situationDescription;
    private String aircraftType;
    private String departure;
    private String arrival;
    private String boardingDate;

    public static QuestionStep3RandomResponse of(List<Long> questionIdList, QuestionStep3 question) {
        return new QuestionStep3RandomResponse(
                questionIdList,
                question.getSituationDescription(),
                question.getAircraftType(),
                question.getDeparture(),
                question.getArrival(),
                question.getBoardingDate()
        );
    }
}
