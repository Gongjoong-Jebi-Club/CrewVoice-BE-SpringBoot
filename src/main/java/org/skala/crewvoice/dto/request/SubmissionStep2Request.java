package org.skala.crewvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * STEP2 음성 파일 제출 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStep2Request {

    @NotNull(message = "평가 ID는 필수입니다.")
    private Long evaluationId;

    @NotNull(message = "문제 ID는 필수입니다.")
    private Long questionId;

    @NotBlank(message = "언어는 필수입니다.")
    @Pattern(regexp = "^(ko-KR|en-US)$", message = "언어는 ko-KR 또는 en-US여야 합니다.")
    private String language; // "ko-KR" or "en-US"
}
