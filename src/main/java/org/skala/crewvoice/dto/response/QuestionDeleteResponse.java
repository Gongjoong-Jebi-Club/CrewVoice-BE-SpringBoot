package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 삭제 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDeleteResponse {

    /**
     * 삭제된 문제 ID
     */
    private Long questionId;

    /**
     * 정적 팩토리 메서드
     */
    public static QuestionDeleteResponse of(Long questionId) {
        return QuestionDeleteResponse.builder()
                .questionId(questionId)
                .build();
    }
}