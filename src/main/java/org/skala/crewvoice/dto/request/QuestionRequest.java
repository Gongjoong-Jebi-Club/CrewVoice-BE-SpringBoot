package org.skala.crewvoice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private Long questionId;
    private String aircraftType;
    private String situationDescription;
    private String explanation;
}
