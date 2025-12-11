package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.EvaluationType;
import org.skala.crewvoice.validation.ValidDateRange;
import org.skala.crewvoice.validation.ValidWeightSum;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

/**
 * 평가(시험) 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평가 생성 요청")
@ValidWeightSum
@ValidDateRange
public class EvaluationCreateRequest {

    @NotBlank(message = "평가 이름은 필수입니다")
    @Size(max = 100, message = "평가 이름은 100자를 초과할 수 없습니다")
    @JsonProperty("name")
    @Schema(description = "평가 이름", example = "2025 1분기 정기 평가", required = true)
    private String name;

    @JsonProperty("evaluation_type")
    @Schema(description = "평가 유형 (EXAM: 실전 평가, MOCK: 모의 평가). 미입력 시 기본값: EXAM", example = "EXAM", allowableValues = {"EXAM", "MOCK"})
    private EvaluationType evaluationType;

    @NotNull(message = "시작 날짜는 필수입니다")
    @JsonProperty("start_date")
    @Schema(description = "평가 시작 일시 (ISO-8601 형식)", example = "2025-01-10T10:00:00Z", required = true)
    private OffsetDateTime startDate;

    @NotNull(message = "종료 날짜는 필수입니다")
    @JsonProperty("end_date")
    @Schema(description = "평가 종료 일시 (ISO-8601 형식)", example = "2025-01-25T12:00:00Z", required = true)
    private OffsetDateTime endDate;

    @NotNull(message = "발음 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_pronunciation")
    @Schema(description = "발음 평가 가중치", example = "20", required = true)
    private Integer weightPronunciation;

    @NotNull(message = "억양 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_intonation")
    @Schema(description = "억양 평가 가중치", example = "20", required = true)
    private Integer weightIntonation;

    @NotNull(message = "강세 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_accent")
    @Schema(description = "강세 평가 가중치", example = "20", required = true)
    private Integer weightAccent;

    @NotNull(message = "쉼 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_pause")
    @Schema(description = "쉼/끊어읽기 평가 가중치", example = "20", required = true)
    private Integer weightPause;

    @NotNull(message = "톤 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_tone")
    @Schema(description = "톤 평가 가중치", example = "10", required = true)
    private Integer weightTone;

    @NotNull(message = "속도 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    @JsonProperty("weight_speed")
    @Schema(description = "속도 평가 가중치", example = "10", required = true)
    private Integer weightSpeed;
}