package org.skala.crewvoice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 평가 목록 응답 DTO (Wrapper)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평가 목록 응답")
public class EvaluationListResponse {

    @Schema(description = "평가 목록")
    private List<EvaluationResponse> evaluations;

    public static EvaluationListResponse of(List<EvaluationResponse> evaluations) {
        return new EvaluationListResponse(evaluations);
    }
}