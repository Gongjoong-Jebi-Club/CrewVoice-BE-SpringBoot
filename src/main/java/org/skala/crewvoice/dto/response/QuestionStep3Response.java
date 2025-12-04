package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep3;

import java.time.OffsetDateTime;

/**
 * STEP3 8 Q� DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionStep3Response {

    private Long questionId;
    private String situationDescription;
    private String aircraftType;
    private String arrival;
    private String departure;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String boardingDate;

    private String scriptKorean;
    private String scriptEnglish;
    private String explanation;
    private String isUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    /**
     * Entity � DTO �X
     */
    public static QuestionStep3Response from(QuestionStep3 entity) {
        return QuestionStep3Response.builder()
                .questionId(entity.getQuestionId())
                .situationDescription(entity.getSituationDescription())
                .aircraftType(entity.getAircraftType())
                .arrival(entity.getArrival())
                .departure(entity.getDeparture())
                .boardingDate(entity.getBoardingDate())
                .scriptKorean(entity.getScriptKorean())
                .scriptEnglish(entity.getScriptEnglish())
                .explanation(entity.getExplanation())
                .isUsed(entity.getIsUsed())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
