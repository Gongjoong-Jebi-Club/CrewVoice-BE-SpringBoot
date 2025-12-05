package org.skala.crewvoice.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class Step1ResultResponse {

    private final List<Step1QuestionResultResponse> questions;

    private Step1ResultResponse(List<Step1QuestionResultResponse> questions) {
        this.questions = questions;
    }

    /**
     * 문제별 결과 리스트로 Step1ResultResponse 생성
     *
     * @param questions 문제별 결과 리스트
     * @return Step1ResultResponse
     */
    public static Step1ResultResponse of(List<Step1QuestionResultResponse> questions) {
        return new Step1ResultResponse(questions);
    }
}
