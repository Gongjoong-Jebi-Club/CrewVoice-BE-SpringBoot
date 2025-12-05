package org.skala.crewvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStep1Request {
    @NotNull(message = "평가 ID는 필수입니다.")
    private Long evaluationId;

    @NotNull(message = "문제 ID는 필수입니다.")
    private Long questionId;

    @NotNull(message = "문제 순서는 필수입니다.")
    private Integer questionOrder;

    @NotBlank(message = "답안은 필수입니다.")
    @Pattern(regexp = "^[A-Da-d]$", message = "답안은 A, B, C, D 중 하나여야 합니다.")
    private String userAnswer;
}
