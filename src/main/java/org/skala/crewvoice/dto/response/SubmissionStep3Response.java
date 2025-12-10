package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * STEP3 음성 파일 제출 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStep3Response {

    @JsonProperty("submissionId")
    private Long submissionId;

    @JsonProperty("fileUrl")
    private String fileUrl;

    @JsonProperty("fileSize")
    private Long fileSize;

    @JsonProperty("uploadedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime uploadedAt;

    public static SubmissionStep3Response of(Long submissionId, String fileUrl, Long fileSize, OffsetDateTime uploadedAt) {
        return SubmissionStep3Response.builder()
                .submissionId(submissionId)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .uploadedAt(uploadedAt)
                .build();
    }
}
