package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.Language;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 녹음 파일 다운로드 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordingDownloadRequest {

    @NotNull(message = "평가 ID는 필수입니다.")
    @JsonProperty("evaluation_id")
    private Long evaluationId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "단계는 필수입니다.")
    @Min(value = 2, message = "step은 2 또는 3만 가능합니다.")
    @Max(value = 3, message = "step은 2 또는 3만 가능합니다.")
    @JsonProperty("step")
    private Integer step;

    @Min(value = 1, message = "question은 1~3만 가능합니다.")
    @Max(value = 3, message = "question은 1~3만 가능합니다.")
    @JsonProperty("question")
    private Integer question;

    @NotNull(message = "언어는 필수입니다.")
    @JsonProperty("language")
    private Language language;  // Enum 타입으로 변경
}