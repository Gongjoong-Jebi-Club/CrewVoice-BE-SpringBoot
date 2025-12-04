package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 진행 중인 평가 목록 응답 DTO (Wrapper)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveEvaluationListResponse {

    @JsonProperty("evaluations")
    private List<ActiveEvaluationResponse> evaluations;

    public static ActiveEvaluationListResponse of(List<ActiveEvaluationResponse> evaluations) {
        return ActiveEvaluationListResponse.builder()
                .evaluations(evaluations)
                .build();
    }
}