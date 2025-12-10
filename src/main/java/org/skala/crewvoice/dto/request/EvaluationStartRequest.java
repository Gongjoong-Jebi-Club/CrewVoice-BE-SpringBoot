package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 평가 시작 요청 DTO (3번 API)
 * 사용자가 제출한 음성 파일에 대한 평가를 시작하는 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationStartRequest {

    @JsonProperty("submission_id")
    @NotNull(message = "제출 ID는 필수입니다.")
    private Long submissionId;

    @JsonProperty("step")
    @NotNull(message = "평가 단계(step)는 필수입니다.")
    @Min(value = 2, message = "step은 2 또는 3이어야 합니다.")
    @Max(value = 3, message = "step은 2 또는 3이어야 합니다.")
    private Integer step;
}
