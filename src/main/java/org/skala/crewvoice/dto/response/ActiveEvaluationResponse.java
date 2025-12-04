package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.Evaluation;

import java.time.OffsetDateTime;

/**
 * 진행 중인 평가 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveEvaluationResponse {

    @JsonProperty("evaluation_id")
    private Long evaluationId;

    @JsonProperty("evaluation_name")
    private String evaluationName;

    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime startDate;

    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime endDate;

    @JsonProperty("evaluation_type")
    private String evaluationType;

    /**
     * Evaluation Entity로부터 ActiveEvaluationResponse 생성
     */
    public static ActiveEvaluationResponse from(Evaluation evaluation) {
        return ActiveEvaluationResponse.builder()
                .evaluationId(evaluation.getEvaluationId())
                .evaluationName(evaluation.getEvaluationName())
                .startDate(evaluation.getStartDate())
                .endDate(evaluation.getEndDate())
                .evaluationType(evaluation.getEvaluationType().name())  // Enum → String 변환
                .build();
    }
}