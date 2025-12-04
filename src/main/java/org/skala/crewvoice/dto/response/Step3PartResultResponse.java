package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.EvaluationStep3;

@Getter
public class Step3PartResultResponse {

    private final Integer partOrder;
    private final Step3LanguageResultResponse koreanResult;
    private final Step3LanguageResultResponse englishResult;

    private Step3PartResultResponse(Integer partOrder,
                                    Step3LanguageResultResponse koreanResult,
                                    Step3LanguageResultResponse englishResult) {
        this.partOrder = partOrder;
        this.koreanResult = koreanResult;
        this.englishResult = englishResult;
    }

    /**
     * part_order와 한국어/영어 평가 결과로 Step3PartResultResponse 생성
     *
     * @param partOrder 파트 순서
     * @param koreanEval 한국어 평가 결과
     * @param englishEval 영어 평가 결과
     * @return Step3PartResultResponse
     */
    public static Step3PartResultResponse fromEvaluations(Integer partOrder,
                                                          EvaluationStep3 koreanEval,
                                                          EvaluationStep3 englishEval) {
        Step3LanguageResultResponse koreanResult = Step3LanguageResultResponse.from(koreanEval);
        Step3LanguageResultResponse englishResult = Step3LanguageResultResponse.from(englishEval);

        return new Step3PartResultResponse(partOrder, koreanResult, englishResult);
    }
}
