package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.EvaluationStep2;
import org.skala.crewvoice.domain.EvaluationStep3;
import org.skala.crewvoice.dto.request.CallbackRequest;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationStep2Repository;
import org.skala.crewvoice.repository.EvaluationStep3Repository;
import org.skala.crewvoice.util.ScoreCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 평가 콜백 처리 전담 서비스
 * FastAPI로부터의 평가 완료 콜백을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluationCallbackService {

    private final EvaluationStep2Repository evaluationStep2Repository;
    private final EvaluationStep3Repository evaluationStep3Repository;
    private final FinalGradeService finalGradeService;

    /**
     * FastAPI 평가 완료 콜백 처리 (5번 API)
     * FastAPI에서 평가가 완료되면 호출되는 콜백 API
     * Step2와 Step3 모두 처리
     *
     * @param request FastAPI 콜백 요청 데이터
     */
    @Transactional
    public void handleCallback(CallbackRequest request) {
        log.info("평가 완료 콜백 수신 - taskId: {}, status: {}", request.getTaskId(), request.getStatus());

        // Task ID로 평가 결과 조회 (Step2 먼저, 없으면 Step3)
        java.util.Optional<EvaluationStep2> step2Evaluation = evaluationStep2Repository
                .findByTaskId(request.getTaskId());

        if (step2Evaluation.isPresent()) {
            // Step2 평가 콜백 처리
            handleStep2Callback(step2Evaluation.get(), request);
        } else {
            // Step3 평가 조회
            EvaluationStep3 step3Evaluation = evaluationStep3Repository
                    .findByTaskId(request.getTaskId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "평가 결과를 찾을 수 없습니다. taskId: " + request.getTaskId(),
                            org.skala.crewvoice.exception.ErrorCode.EVALUATION_NOT_FOUND
                    ));

            // Step3 평가 콜백 처리
            handleStep3Callback(step3Evaluation, request);
        }
    }

    /**
     * Step2 평가 콜백 처리
     */
    private void handleStep2Callback(EvaluationStep2 evaluation, CallbackRequest request) {
        log.info("Step2 평가 레코드 조회 완료 - evaluationStep2Id: {}, currentStatus: {}",
                evaluation.getId(), evaluation.getStatus());

        // 평가 상태에 따라 처리
        if ("completed".equalsIgnoreCase(request.getStatus())) {
            // 성공: 점수 업데이트
            CallbackRequest.ScoresDTO scores = request.getScores();

            if (scores == null) {
                log.error("평가 완료되었으나 점수 데이터가 없습니다 - taskId: {}", request.getTaskId());
                evaluation.markAsFailed("점수 데이터 누락");
            } else {
                // 1. FastAPI 점수를 저장 (overallScore는 임시로 저장)
                evaluation.updateScores(
                        scores.getScorePronunciation(),
                        scores.getScorePause(),
                        scores.getScoreIntonation(),
                        scores.getScoreTone(),
                        scores.getScoreSpeed(),
                        scores.getScoreAccent(),
                        scores.getOverallScore(),
                        request.getFeedbackSummary()
                );
                evaluation.markAsCompleted();

                log.info("Step2 평가 점수 업데이트 완료 - taskId: {}, FastAPI overallScore: {}",
                        request.getTaskId(), scores.getOverallScore());

                // 2. 가중치 적용하여 overallScore 재계산
                try {
                    Evaluation evaluationConfig = evaluation.getSubmission().getEvaluation();
                    BigDecimal calculatedOverallScore = ScoreCalculator.calculateWeightedScore(
                            evaluationConfig,
                            scores.getScorePronunciation(),
                            scores.getScorePause(),
                            scores.getScoreIntonation(),
                            scores.getScoreTone(),
                            scores.getScoreSpeed(),
                            scores.getScoreAccent()
                    );

                    // overallScore 업데이트
                    evaluation.updateOverallScore(calculatedOverallScore);
                    log.info("Step2 overallScore 재계산 완료 - taskId: {}, FastAPI: {}, Calculated: {}",
                            request.getTaskId(), scores.getOverallScore(), calculatedOverallScore);
                } catch (Exception e) {
                    log.error("Step2 overallScore 재계산 실패 - taskId: {}, error: {}",
                            request.getTaskId(), e.getMessage(), e);
                }

                // 3. FinalGrade 업데이트
                try {
                    finalGradeService.updateStep2Score(evaluation);
                    log.info("FinalGrade Step2 점수 업데이트 완료 - taskId: {}", request.getTaskId());
                } catch (Exception e) {
                    log.error("FinalGrade Step2 점수 업데이트 실패 - taskId: {}, error: {}",
                            request.getTaskId(), e.getMessage(), e);
                }
            }

        } else if ("failed".equalsIgnoreCase(request.getStatus())) {
            // 실패: 에러 메시지 저장
            evaluation.markAsFailed(request.getErrorMessage());
            log.warn("Step2 평가 실패 처리 완료 - taskId: {}, errorMessage: {}",
                    request.getTaskId(), request.getErrorMessage());

        } else {
            log.error("알 수 없는 평가 상태 - taskId: {}, status: {}",
                    request.getTaskId(), request.getStatus());
            evaluation.markAsFailed("알 수 없는 상태: " + request.getStatus());
        }

        // 저장
        evaluationStep2Repository.save(evaluation);
        log.info("Step2 평가 콜백 처리 완료 - taskId: {}, finalStatus: {}",
                request.getTaskId(), evaluation.getStatus());
    }

    /**
     * Step3 평가 콜백 처리
     */
    private void handleStep3Callback(EvaluationStep3 evaluation, CallbackRequest request) {
        log.info("Step3 평가 레코드 조회 완료 - evaluationStep3Id: {}, currentStatus: {}, language: {}",
                evaluation.getId(), evaluation.getStatus(), evaluation.getLanguage().getCode());

        // 평가 상태에 따라 처리
        if ("completed".equalsIgnoreCase(request.getStatus())) {
            // 성공: 점수 업데이트
            CallbackRequest.ScoresDTO scores = request.getScores();

            if (scores == null) {
                log.error("평가 완료되었으나 점수 데이터가 없습니다 - taskId: {}", request.getTaskId());
                evaluation.markAsFailed("점수 데이터 누락");
            } else {
                // 1. FastAPI 점수를 저장 (overallScore는 임시로 저장)
                evaluation.updateScores(
                        scores.getScorePronunciation(),
                        scores.getScorePause(),
                        scores.getScoreIntonation(),
                        scores.getScoreTone(),
                        scores.getScoreSpeed(),
                        scores.getScoreAccent(),
                        scores.getOverallScore(),
                        request.getFeedbackSummary()
                );
                evaluation.markAsCompleted();

                log.info("Step3 평가 점수 업데이트 완료 - taskId: {}, language: {}, FastAPI overallScore: {}",
                        request.getTaskId(), evaluation.getLanguage().getCode(), scores.getOverallScore());

                // 2. 가중치 적용하여 overallScore 재계산
                try {
                    Evaluation evaluationConfig = evaluation.getSubmission().getEvaluation();
                    BigDecimal calculatedOverallScore = ScoreCalculator.calculateWeightedScore(
                            evaluationConfig,
                            scores.getScorePronunciation(),
                            scores.getScorePause(),
                            scores.getScoreIntonation(),
                            scores.getScoreTone(),
                            scores.getScoreSpeed(),
                            scores.getScoreAccent()
                    );

                    // overallScore 업데이트
                    evaluation.updateOverallScore(calculatedOverallScore);
                    log.info("Step3 overallScore 재계산 완료 - taskId: {}, language: {}, FastAPI: {}, Calculated: {}",
                            request.getTaskId(), evaluation.getLanguage().getCode(),
                            scores.getOverallScore(), calculatedOverallScore);
                } catch (Exception e) {
                    log.error("Step3 overallScore 재계산 실패 - taskId: {}, language: {}, error: {}",
                            request.getTaskId(), evaluation.getLanguage().getCode(), e.getMessage(), e);
                }

                // 3. FinalGrade 업데이트
                try {
                    finalGradeService.updateStep3Score(evaluation);
                    log.info("FinalGrade Step3 점수 업데이트 완료 - taskId: {}, language: {}",
                            request.getTaskId(), evaluation.getLanguage().getCode());
                } catch (Exception e) {
                    log.error("FinalGrade Step3 점수 업데이트 실패 - taskId: {}, language: {}, error: {}",
                            request.getTaskId(), evaluation.getLanguage().getCode(), e.getMessage(), e);
                }
            }

        } else if ("failed".equalsIgnoreCase(request.getStatus())) {
            // 실패: 에러 메시지 저장
            evaluation.markAsFailed(request.getErrorMessage());
            log.warn("Step3 평가 실패 처리 완료 - taskId: {}, language: {}, errorMessage: {}",
                    request.getTaskId(), evaluation.getLanguage().getCode(), request.getErrorMessage());

        } else {
            log.error("알 수 없는 평가 상태 - taskId: {}, status: {}",
                    request.getTaskId(), request.getStatus());
            evaluation.markAsFailed("알 수 없는 상태: " + request.getStatus());
        }

        // 저장
        evaluationStep3Repository.save(evaluation);
        log.info("Step3 평가 콜백 처리 완료 - taskId: {}, language: {}, finalStatus: {}",
                request.getTaskId(), evaluation.getLanguage().getCode(), evaluation.getStatus());
    }
}
