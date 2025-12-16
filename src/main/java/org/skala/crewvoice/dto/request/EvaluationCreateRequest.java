package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.EvaluationType;
import org.skala.crewvoice.validation.ValidDateRange;
import org.skala.crewvoice.validation.ValidFastAPIWeights;
import org.skala.crewvoice.validation.ValidWeightSum;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
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
@ValidFastAPIWeights
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

    // ===== FastAPI 파라미터 가중치 =====

    // Tone (4개)
    @NotNull(message = "Clarity 가중치는 필수입니다")
    @DecimalMin(value = "0.0", message = "Clarity 가중치는 0.0 이상이어야 합니다")
    @DecimalMax(value = "1.0", message = "Clarity 가중치는 1.0 이하여야 합니다")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_clarity")
    @Schema(description = "Tone - Clarity 가중치 (0.0~1.0, 0.05 단위)", example = "0.250", required = true)
    private BigDecimal weightClarity;

    @NotNull(message = "Stability 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_stability")
    @Schema(description = "Tone - Stability 가중치 (0.0~1.0, 0.05 단위)", example = "0.250", required = true)
    private BigDecimal weightStability;

    @NotNull(message = "Brightness 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_brightness")
    @Schema(description = "Tone - Brightness 가중치 (0.0~1.0, 0.05 단위)", example = "0.250", required = true)
    private BigDecimal weightBrightness;

    @NotNull(message = "Pitch Position 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_pitch_position")
    @Schema(description = "Tone - Pitch Position 가중치 (0.0~1.0, 0.05 단위)", example = "0.250", required = true)
    private BigDecimal weightPitchPosition;

    // Pause (3개)
    @NotNull(message = "Fluency 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_fluency")
    @Schema(description = "Pause - Fluency 가중치 (0.0~1.0, 0.05 단위)", example = "0.350", required = true)
    private BigDecimal weightFluency;

    @NotNull(message = "Position 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_position")
    @Schema(description = "Pause - Position 가중치 (0.0~1.0, 0.05 단위)", example = "0.350", required = true)
    private BigDecimal weightPosition;

    @NotNull(message = "Length 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_length")
    @Schema(description = "Pause - Length 가중치 (0.0~1.0, 0.05 단위)", example = "0.300", required = true)
    private BigDecimal weightLength;

    // Accent (2개)
    @NotNull(message = "Pitch 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_pitch")
    @Schema(description = "Accent - Pitch 가중치 (0.0~1.0, 0.05 단위)", example = "0.500", required = true)
    private BigDecimal weightPitch;

    @NotNull(message = "Energy 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_energy")
    @Schema(description = "Accent - Energy 가중치 (0.0~1.0, 0.05 단위)", example = "0.500", required = true)
    private BigDecimal weightEnergy;

    // Intonation (2개)
    @NotNull(message = "Baseline 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_baseline")
    @Schema(description = "Intonation - Baseline 가중치 (0.0~1.0, 0.05 단위)", example = "0.500", required = true)
    private BigDecimal weightBaseline;

    @NotNull(message = "Iso 가중치는 필수입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Digits(integer = 1, fraction = 3)
    @JsonProperty("weight_iso")
    @Schema(description = "Intonation - Iso 가중치 (0.0~1.0, 0.05 단위)", example = "0.500", required = true)
    private BigDecimal weightIso;

    // Speed (1개)
    @NotNull(message = "Deviation Threshold는 필수입니다")
    @DecimalMin(value = "5.0")
    @DecimalMax(value = "10.0")
    @Digits(integer = 2, fraction = 1)
    @JsonProperty("deviation_threshold")
    @Schema(description = "Speed - Deviation Threshold (5.0~10.0, 0.5 단위)", example = "7.5", required = true)
    private BigDecimal deviationThreshold;
}