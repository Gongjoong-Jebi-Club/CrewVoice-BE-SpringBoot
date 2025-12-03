package org.skala.crewvoice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep3Request {
    private Long questionId; // nullable: null이면 추가, 값이 있으면 수정
    private String aircraftType;
    private String departure;
    private String arrival;
    private String boardingDate;
    private String situationDescription;
    private String scriptKorean;
    private String scriptEnglish;
    private String explanation;
}
