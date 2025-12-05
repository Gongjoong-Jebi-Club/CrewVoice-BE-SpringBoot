package org.skala.crewvoice.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class Step3ResultResponse {

    private final Long userId;
    private final List<Step3PartResultResponse> parts;

    private Step3ResultResponse(Long userId, List<Step3PartResultResponse> parts) {
        this.userId = userId;
        this.parts = parts;
    }

    /**
     * userId와 파트별 평가 결과 리스트로 Step3ResultResponse 생성
     *
     * @param userId 사용자 ID
     * @param parts 파트별 평가 결과 리스트
     * @return Step3ResultResponse
     */
    public static Step3ResultResponse of(Long userId, List<Step3PartResultResponse> parts) {
        return new Step3ResultResponse(userId, parts);
    }
}
