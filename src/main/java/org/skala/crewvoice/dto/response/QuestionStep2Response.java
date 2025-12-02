package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep2;

import java.time.OffsetDateTime;

/**
 * STEP2 문제 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionStep2Response {

    private Long questionId;
    private String situationDescription;
    private String aircraftType;
    private String scriptKorean;
    private String scriptEnglish;
    private String explanation;
    private String isUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static QuestionStep2Response from(QuestionStep2 entity) {
        return QuestionStep2Response.builder()
                .questionId(entity.getQuestionId())
                .situationDescription(entity.getSituationDescription())
                .aircraftType(entity.getAircraftType())
                .scriptKorean(entity.getScriptKorean())
                .scriptEnglish(entity.getScriptEnglish())
                .explanation(entity.getExplanation())
                .isUsed(entity.getIsUsed())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}