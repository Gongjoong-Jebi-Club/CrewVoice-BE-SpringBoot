// src/main/java/org/skala/crewvoice/dto/response/EvaluationResponse.java
package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.Evaluation;

import java.time.OffsetDateTime;

/**
 * 평가 목록 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 정보")
public class EvaluationResponse {

    @JsonProperty("evaluation_id")
    @Schema(description = "평가 ID", example = "3")
    private String evaluationId;

    @JsonProperty("evaluation_name")
    @Schema(description = "평가 이름", example = "2025 1분기 정기 평가")
    private String evaluationName;

    @JsonProperty("start_date")
    @Schema(description = "평가 시작 일시 (ISO-8601 형식)", example = "2025-01-15T09:00:00Z")
    private OffsetDateTime startDate;

    @JsonProperty("end_date")
    @Schema(description = "평가 종료 일시 (ISO-8601 형식)", example = "2025-01-15T18:00:00Z")
    private OffsetDateTime endDate;

    @JsonProperty("target_count")
    @Schema(description = "평가 대상 인원 수 (시험 생성 시 가입되어 있는 피평가자 수)", example = "12")
    private Integer targetCount;

    @JsonProperty("completed_count")
    @Schema(description = "평가 완료 인원 수", example = "10")
    private Integer completedCount;

    /**
     * Entity와 통계 정보로부터 Response DTO 생성
     */
    public static EvaluationResponse of(Evaluation evaluation, Integer targetCount, Integer completedCount) {
        return EvaluationResponse.builder()
                .evaluationId(String.valueOf(evaluation.getEvaluationId()))
                .evaluationName(evaluation.getEvaluationName())
                .startDate(evaluation.getStartDate())
                .endDate(evaluation.getEndDate())
                .targetCount(targetCount)
                .completedCount(completedCount)
                .build();
    }
}