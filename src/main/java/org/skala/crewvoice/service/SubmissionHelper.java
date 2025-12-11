package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.Language;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.springframework.stereotype.Component;

/**
 * Submission 서비스들의 공통 로직을 제공하는 헬퍼 클래스
 * Step2, Step3 서비스에서 중복되는 로직을 제거합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionHelper {

    private final EvaluationRepository evaluationRepository;

    /**
     * Evaluation 조회 및 검증
     *
     * @param evaluationId 평가 ID
     * @return 조회된 Evaluation
     * @throws EntityNotFoundException 평가를 찾을 수 없는 경우
     */
    public Evaluation findEvaluation(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));
    }

    /**
     * 언어 코드를 Language Enum으로 변환
     *
     * @param languageCode 언어 코드 문자열 (예: "KOR", "ENG")
     * @return Language Enum
     * @throws IllegalArgumentException 유효하지 않은 언어 코드인 경우
     */
    public Language parseLanguage(String languageCode) {
        return Language.fromCode(languageCode);
    }
}
