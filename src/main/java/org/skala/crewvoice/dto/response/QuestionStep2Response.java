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

    /**
     * Entity → DTO 변환 (리스트 조회용 - 필수 필드만)
     */
    public static QuestionStep2Response from(QuestionStep2 entity) {
        return QuestionStep2Response.builder()
                .questionId(entity.getQuestionId())
                .situationDescription(entity.getSituationDescription())
                .aircraftType(entity.getAircraftType())
                .build();
    }
}