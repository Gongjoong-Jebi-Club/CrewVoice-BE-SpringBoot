package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 평가 시작 응답 DTO (3번 API)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 시작 응답")
public class EvaluationStartResponse {

    @JsonProperty("submission_id")
    @Schema(description = "제출 ID", example = "123")
    private Long submissionId;

    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "평가가 시작되었습니다. 백그라운드에서 처리 중입니다.")
    private String message;

    /**
     * 평가 시작 응답 생성 (성공)
     */
    public static EvaluationStartResponse success(Long submissionId) {
        return EvaluationStartResponse.builder()
                .submissionId(submissionId)
                .message("평가가 시작되었습니다. 백그라운드에서 처리 중입니다.")
                .build();
    }
}