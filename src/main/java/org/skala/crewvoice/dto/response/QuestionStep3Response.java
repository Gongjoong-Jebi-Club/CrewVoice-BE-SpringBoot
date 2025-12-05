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

    /**
     * Entity → DTO 변환 (리스트 조회용 - 필수 필드만)
     */
    public static QuestionStep3Response from(QuestionStep3 entity) {
        return QuestionStep3Response.builder()
                .questionId(entity.getQuestionId())
                .situationDescription(entity.getSituationDescription())
                .aircraftType(entity.getAircraftType())
                .build();
    }
}
