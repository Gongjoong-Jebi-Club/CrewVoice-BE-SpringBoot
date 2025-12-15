package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.SubmissionStep1Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 최종 성적 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinalGradeService {

    private final FinalGradeRepository finalGradeRepository;
    private final SubmissionStep1Repository submissionStep1Repository;

    /**
     * STEP1 완료 시 초기 FinalGrade 레코드 생성
     * 20개 문제를 모두 제출한 경우 호출
     *
     * @param userId 사용자 ID
     * @param evaluation 평가
     */
    @Transactional
    public void createInitialFinalGrade(Long userId, Evaluation evaluation, User user) {
        log.info("FinalGrade 초기 레코드 생성 시작 - userId: {}, evaluationId: {}",
                userId, evaluation.getEvaluationId());

        // 이미 존재하는지 확인 (중복 방지)
        FinalGrade existing = finalGradeRepository.findByUserIdAndEvaluationId(
                userId, evaluation.getEvaluationId());

        if (existing != null) {
            log.info("이미 FinalGrade 레코드가 존재합니다 - userId: {}, evaluationId: {}",
                    userId, evaluation.getEvaluationId());
            return;
        }

        // Step1 정답 개수 조회
        long correctCount = submissionStep1Repository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(userId)
                        && s.getEvaluation().getEvaluationId().equals(evaluation.getEvaluationId())
                        && s.getIsCorrect())
                .count();

        // Step1 점수 계산 (정답 개수 × 5)
        BigDecimal step1Score = BigDecimal.valueOf(correctCount * 5);

        // FinalGrade 생성
        FinalGrade finalGrade = FinalGrade.builder()
                .user(user)
                .evaluation(evaluation)
                .step1Score(step1Score)
                .build();

        finalGradeRepository.save(finalGrade);

        log.info("FinalGrade 초기 레코드 생성 완료 - userId: {}, evaluationId: {}, step1Score: {}",
                userId, evaluation.getEvaluationId(), step1Score);
    }

    /**
     * Step2 평가 완료 시 점수 업데이트
     *
     * @param evaluationStep2 Step2 평가 결과
     */
    @Transactional
    public void updateStep2Score(EvaluationStep2 evaluationStep2) {
        SubmissionStep2 submission = evaluationStep2.getSubmission();
        Long userId = submission.getUser().getId();
        Long evaluationId = submission.getEvaluation().getEvaluationId();
        Language language = evaluationStep2.getLanguage();

        log.info("Step2 점수 업데이트 시작 - userId: {}, evaluationId: {}, language: {}",
                userId, evaluationId, language.getCode());

        // FinalGrade 조회
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(userId, evaluationId);

        if (finalGrade == null) {
            log.warn("FinalGrade 레코드가 존재하지 않습니다 - userId: {}, evaluationId: {}", userId, evaluationId);
            return;
        }

        // 가중 평균 점수 계산
        BigDecimal weightedScore = calculateWeightedScore(
                submission.getEvaluation(),
                evaluationStep2.getScorePronunciation(),
                evaluationStep2.getScorePause(),
                evaluationStep2.getScoreIntonation(),
                evaluationStep2.getScoreTone(),
                evaluationStep2.getScoreSpeed(),
                evaluationStep2.getScoreAccent()
        );

        // 언어에 따라 업데이트
        if (language == Language.KOREAN) {
            finalGrade.updateStep2KorScore(weightedScore);
            log.info("Step2 한국어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}",
                    userId, evaluationId, weightedScore);
        } else if (language == Language.ENGLISH) {
            finalGrade.updateStep2EngScore(weightedScore);
            log.info("Step2 영어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}",
                    userId, evaluationId, weightedScore);
        }

        finalGradeRepository.save(finalGrade);
    }

    /**
     * Step3 평가 완료 시 점수 업데이트
     * Step3는 3개 문제의 평균을 저장
     *
     * @param evaluationStep3 Step3 평가 결과
     */
    @Transactional
    public void updateStep3Score(EvaluationStep3 evaluationStep3) {
        SubmissionStep3 submission = evaluationStep3.getSubmission();
        Long userId = submission.getUser().getId();
        Long evaluationId = submission.getEvaluation().getEvaluationId();
        Language language = evaluationStep3.getLanguage();

        log.info("Step3 점수 업데이트 시작 - userId: {}, evaluationId: {}, language: {}, partOrder: {}",
                userId, evaluationId, language.getCode(), submission.getPartOrder());

        // FinalGrade 조회
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(userId, evaluationId);

        if (finalGrade == null) {
            log.warn("FinalGrade 레코드가 존재하지 않습니다 - userId: {}, evaluationId: {}", userId, evaluationId);
            return;
        }

        // 가중 평균 점수 계산
        BigDecimal weightedScore = calculateWeightedScore(
                submission.getEvaluation(),
                evaluationStep3.getScorePronunciation(),
                evaluationStep3.getScorePause(),
                evaluationStep3.getScoreIntonation(),
                evaluationStep3.getScoreTone(),
                evaluationStep3.getScoreSpeed(),
                evaluationStep3.getScoreAccent()
        );

        // 언어에 따라 업데이트
        if (language == Language.KOREAN) {
            finalGrade.updateStep3KorScore(weightedScore);
            log.info("Step3 한국어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}",
                    userId, evaluationId, weightedScore);
        } else if (language == Language.ENGLISH) {
            finalGrade.updateStep3EngScore(weightedScore);
            log.info("Step3 영어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}",
                    userId, evaluationId, weightedScore);
        }

        finalGradeRepository.save(finalGrade);
    }

    /**
     * 가중 평균 점수 계산
     * 각 항목 점수에 가중치를 곱한 후 합산 (가중치 합 = 100)
     *
     * @param evaluation 평가 (가중치 정보 포함)
     * @param scorePronunciation 발음 점수
     * @param scorePause 쉼 점수
     * @param scoreIntonation 억양 점수
     * @param scoreTone 톤 점수
     * @param scoreSpeed 속도 점수
     * @param scoreAccent 강세 점수
     * @return 가중 평균 점수
     */
    private BigDecimal calculateWeightedScore(
            Evaluation evaluation,
            BigDecimal scorePronunciation,
            BigDecimal scorePause,
            BigDecimal scoreIntonation,
            BigDecimal scoreTone,
            BigDecimal scoreSpeed,
            BigDecimal scoreAccent
    ) {
        BigDecimal weightedSum = BigDecimal.ZERO;

        // 각 항목 점수 × 가중치 / 100
        weightedSum = weightedSum.add(scorePronunciation.multiply(evaluation.getWeightPronunciation()));
        weightedSum = weightedSum.add(scorePause.multiply(evaluation.getWeightPause()));
        weightedSum = weightedSum.add(scoreIntonation.multiply(evaluation.getWeightIntonation()));
        weightedSum = weightedSum.add(scoreTone.multiply(evaluation.getWeightTone()));
        weightedSum = weightedSum.add(scoreSpeed.multiply(evaluation.getWeightSpeed()));
        weightedSum = weightedSum.add(scoreAccent.multiply(evaluation.getWeightAccent()));

        // 가중치 합계가 100이므로 100으로 나눔
        return weightedSum.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
