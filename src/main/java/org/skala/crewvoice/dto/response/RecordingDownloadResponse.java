package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.Language;

/**
 * 녹음 파일 다운로드 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordingDownloadResponse {

    @JsonProperty("evaluation_id")
    private Long evaluationId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("step")
    private Integer step;

    @JsonProperty("question")
    private Integer question;  // STEP3만 해당 (1, 2, 3)

    @JsonProperty("language")
    private Language language;  // Enum 타입으로 변경

    @JsonProperty("file_url")
    private String fileUrl;  // Pre-signed URL

    @JsonProperty("expires_in")
    private Integer expiresIn;  // 초 단위 (900 = 15분)

    @JsonProperty("file_name")
    private String fileName;
}