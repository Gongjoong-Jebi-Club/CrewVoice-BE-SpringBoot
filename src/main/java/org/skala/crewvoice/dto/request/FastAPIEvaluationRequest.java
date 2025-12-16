package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 평가 요청 DTO
 * Spring Boot → FastAPI로 전송하는 음성 평가 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FastAPIEvaluationRequest {

    /**
     * 평가 작업 추적용 UUID
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * 평가할 오디오 파일 URL (S3 Pre-signed URL)
     */
    @JsonProperty("audio_url")
    private String audioUrl;

    /**
     * 평가 기준 스크립트 (정답 텍스트)
     */
    @JsonProperty("script")
    private String script;

    /**
     * 언어 코드 ("ko-KR" 또는 "en-US")
     */
    @JsonProperty("language")
    private String language;

    /**
     * 평가 완료 후 결과를 받을 콜백 URL
     */
    @JsonProperty("callback_url")
    private String callbackUrl;

    /**
     * FastAPI 파라미터 가중치 (gender 포함)
     */
    @JsonProperty("weights")
    private WeightsDTO weights;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeightsDTO {
        @JsonProperty("weight_clarity")
        private Double weightClarity;

        @JsonProperty("weight_stability")
        private Double weightStability;

        @JsonProperty("weight_brightness")
        private Double weightBrightness;

        @JsonProperty("weight_pitch_position")
        private Double weightPitchPosition;

        @JsonProperty("weight_fluency")
        private Double weightFluency;

        @JsonProperty("weight_position")
        private Double weightPosition;

        @JsonProperty("weight_length")
        private Double weightLength;

        @JsonProperty("weight_pitch")
        private Double weightPitch;

        @JsonProperty("weight_energy")
        private Double weightEnergy;

        @JsonProperty("weight_baseline")
        private Double weightBaseline;

        @JsonProperty("weight_iso")
        private Double weightIso;

        @JsonProperty("deviation_threshold")
        private Double deviationThreshold;

        /**
         * 성별 (고정값 "F")
         */
        @JsonProperty("gender")
        private String gender;
    }
}
