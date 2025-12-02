package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep2;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep2RandomResponse {
    private List<Long> questionIdList;
    private String situationDescription;
    private String aircraftType;

    public static QuestionStep2RandomResponse of(QuestionStep2 question, List<Long> questions) {
        return new QuestionStep2RandomResponse(
                questions,
                question.getSituationDescription(),
                question.getAircraftType()
        );
    }
}
