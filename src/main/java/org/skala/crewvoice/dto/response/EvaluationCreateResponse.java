package org.skala.crewvoice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 평가(시험) 생성 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평가 생성 응답")
public class EvaluationCreateResponse {

    @Schema(description = "생성된 평가 ID", example = "3")
    private String id;

    /**
     * Entity로부터 Response DTO 생성
     */
    public static EvaluationCreateResponse from(Long evaluationId) {
        return new EvaluationCreateResponse(String.valueOf(evaluationId));
    }
}