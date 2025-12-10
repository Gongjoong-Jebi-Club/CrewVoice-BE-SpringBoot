package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * FastAPI 평가 완료 콜백 요청 DTO (5번 API)
 * FastAPI → Spring Boot로 전송되는 평가 결과
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CallbackRequest {

    /**
     * 평가 작업 추적용 UUID (3번 API에서 생성한 값)
     */
    @JsonProperty("task_id")
    @NotNull(message = "작업 ID는 필수입니다.")
    private String taskId;

    /**
     * 평가 상태 ("completed" 또는 "failed")
     */
    @JsonProperty("status")
    @NotNull(message = "평가 상태는 필수입니다.")
    private String status;

    /**
     * 평가 점수 (status가 "completed"일 때만 존재)
     */
    @JsonProperty("scores")
    private ScoresDTO scores;

    /**
     * 평가 피드백 요약 (status가 "completed"일 때만 존재)
     */
    @JsonProperty("feedback_summary")
    private String feedbackSummary;

    /**
     * 오류 메시지 (status가 "failed"일 때만 존재)
     */
    @JsonProperty("error_message")
    private String errorMessage;

    /**
     * 평가 점수 내부 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoresDTO {

        @JsonProperty("score_pronunciation")
        private BigDecimal scorePronunciation;

        @JsonProperty("score_pause")
        private BigDecimal scorePause;

        @JsonProperty("score_intonation")
        private BigDecimal scoreIntonation;

        @JsonProperty("score_tone")
        private BigDecimal scoreTone;

        @JsonProperty("score_speed")
        private BigDecimal scoreSpeed;

        @JsonProperty("score_accent")
        private BigDecimal scoreAccent;

        @JsonProperty("overall_score")
        private BigDecimal overallScore;
    }
}
