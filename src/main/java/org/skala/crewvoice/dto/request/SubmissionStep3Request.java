package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * STEP3 음성 파일 제출 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStep3Request {

    @NotNull(message = "평가 ID는 필수입니다.")
    @JsonProperty("evaluationId")
    private Long evaluationId;

    @NotNull(message = "문제 ID는 필수입니다.")
    @JsonProperty("questionId")
    private Long questionId;

    @NotNull(message = "파트 순서는 필수입니다.")
    @Min(value = 1, message = "파트 순서는 1 이상이어야 합니다.")
    @JsonProperty("partOrder")
    private Integer partOrder; // 1, 2, 3

    @NotBlank(message = "언어는 필수입니다.")
    @Pattern(regexp = "^(ko-KR|en-US|zh-CN)$", message = "언어는 ko-KR, en-US, zh-CN 중 하나여야 합니다.")
    @JsonProperty("language")
    private String language; // "ko-KR", "en-US", "zh-CN"
}
