package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.EvaluationStep2;

@Getter
public class Step2ResultResponse {

    private final Long userId;
    private final Step2LanguageResultResponse koreanResult;
    private final Step2LanguageResultResponse englishResult;

    private Step2ResultResponse(Long userId, Step2LanguageResultResponse koreanResult,
                                Step2LanguageResultResponse englishResult) {
        this.userId = userId;
        this.koreanResult = koreanResult;
        this.englishResult = englishResult;
    }

    /**
     * 한국어와 영어 평가 결과로 Step2ResultResponse 생성
     *
     * @param userId 사용자 ID
     * @param koreanEval 한국어 평가 결과
     * @param englishEval 영어 평가 결과
     * @return Step2ResultResponse
     */
    public static Step2ResultResponse fromEvaluations(Long userId,
                                                      EvaluationStep2 koreanEval,
                                                      EvaluationStep2 englishEval) {
        Step2LanguageResultResponse koreanResult = Step2LanguageResultResponse.from(koreanEval);
        Step2LanguageResultResponse englishResult = Step2LanguageResultResponse.from(englishEval);

        return new Step2ResultResponse(userId, koreanResult, englishResult);
    }

    /**
     * 기본값(0점)으로 Step2ResultResponse 생성
     * 평가 결과가 없을 때 사용
     *
     * @param userId 사용자 ID
     * @return 모든 점수가 0인 Step2ResultResponse
     */
    public static Step2ResultResponse empty(Long userId) {
        Step2LanguageResultResponse koreanResult = Step2LanguageResultResponse.empty();
        Step2LanguageResultResponse englishResult = Step2LanguageResultResponse.empty();

        return new Step2ResultResponse(userId, koreanResult, englishResult);
    }
}
