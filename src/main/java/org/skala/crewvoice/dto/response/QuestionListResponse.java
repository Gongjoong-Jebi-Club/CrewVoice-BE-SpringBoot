package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문제 리스트 응답 래퍼 DTO
 * STEP2, STEP3 문제 목록을 "questions" 배열로 감싸서 반환
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListResponse {

    @JsonProperty("questions")
    private List<?> questions;

    public static QuestionListResponse of(List<?> questions) {
        return new QuestionListResponse(questions);
    }
}
