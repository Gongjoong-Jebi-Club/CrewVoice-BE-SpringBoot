package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep2;

import java.time.OffsetDateTime;

/**
 * STEP2 문제 리스트 응답 DTO
 * STEP2 문제 목록 조회용 (페이지네이션) - 전체 필드 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionStep2ListResponse {
    private Long questionId;
    private String situationDescription;
    private String aircraftType;
    private String scriptKorean;
    private String scriptEnglish;
    private String explanation;
    private String isUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    public static QuestionStep2ListResponse from(QuestionStep2 question) {
        return QuestionStep2ListResponse.builder()
                .questionId(question.getQuestionId())
                .situationDescription(question.getSituationDescription())
                .aircraftType(question.getAircraftType())
                .scriptKorean(question.getScriptKorean())
                .scriptEnglish(question.getScriptEnglish())
                .explanation(question.getExplanation())
                .isUsed(question.getIsUsed())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
