package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.repository.EvaluationStep3Repository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.SubmissionStep1Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
    private final EvaluationStep3Repository evaluationStep3Repository;

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
        long correctCount = submissionStep1Repository.countCorrectByUserIdAndEvaluationId(
                userId, evaluation.getEvaluationId());

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
     * Step3는 3개 파트(part 1, 2, 3)의 평균을 저장
     * 3개 파트가 모두 완료되었을 때만 평균을 계산하여 업데이트
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

        // 해당 언어로 완료된 모든 Step3 평가 조회 (최대 3개 - part 1, 2, 3)
        List<EvaluationStep3> completedEvaluations = evaluationStep3Repository
                .findCompletedByUserIdAndEvaluationIdAndLanguage(userId, evaluationId, language);

        log.info("완료된 Step3 평가 개수 - language: {}, count: {}/3", language.getCode(), completedEvaluations.size());

        // 3개 파트가 모두 완료되지 않았으면 아직 업데이트하지 않음
        if (completedEvaluations.size() < 3) {
            log.info("아직 모든 파트가 완료되지 않음 - 평균 계산 대기 중 (현재: {}/3)", completedEvaluations.size());
            return;
        }

        // 3개 파트의 평균 계산
        BigDecimal totalScore = BigDecimal.ZERO;

        for (EvaluationStep3 eval : completedEvaluations) {
            BigDecimal partScore = calculateWeightedScore(
                    submission.getEvaluation(),
                    eval.getScorePronunciation(),
                    eval.getScorePause(),
                    eval.getScoreIntonation(),
                    eval.getScoreTone(),
                    eval.getScoreSpeed(),
                    eval.getScoreAccent()
            );
            totalScore = totalScore.add(partScore);
            log.info("Part {} 점수: {}", eval.getSubmission().getPartOrder(), partScore);
        }

        BigDecimal averageScore = totalScore.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
        log.info("Step3 3개 파트 평균 점수: {} (합계: {} ÷ 3)", averageScore, totalScore);

        // 언어에 따라 업데이트
        if (language == Language.KOREAN) {
            finalGrade.updateStep3KorScore(averageScore);
            log.info("Step3 한국어 점수 업데이트 완료 - userId: {}, evaluationId: {}, avgScore: {}",
                    userId, evaluationId, averageScore);
        } else if (language == Language.ENGLISH) {
            finalGrade.updateStep3EngScore(averageScore);
            log.info("Step3 영어 점수 업데이트 완료 - userId: {}, evaluationId: {}, avgScore: {}",
                    userId, evaluationId, averageScore);
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
        // FastAPI가 반환한 점수 로깅
        log.info("========== FastAPI 점수 상세 ==========");
        log.info("발음(Pronunciation): {} (가중치: {})", scorePronunciation, evaluation.getWeightPronunciation());
        log.info("쉼(Pause): {} (가중치: {})", scorePause, evaluation.getWeightPause());
        log.info("억양(Intonation): {} (가중치: {})", scoreIntonation, evaluation.getWeightIntonation());
        log.info("톤(Tone): {} (가중치: {})", scoreTone, evaluation.getWeightTone());
        log.info("속도(Speed): {} (가중치: {})", scoreSpeed, evaluation.getWeightSpeed());
        log.info("강세(Accent): {} (가중치: {})", scoreAccent, evaluation.getWeightAccent());

        BigDecimal weightedSum = BigDecimal.ZERO;

        // 각 항목 점수 × 가중치 계산 및 로깅
        BigDecimal weightedPronunciation = scorePronunciation.multiply(evaluation.getWeightPronunciation());
        BigDecimal weightedPause = scorePause.multiply(evaluation.getWeightPause());
        BigDecimal weightedIntonation = scoreIntonation.multiply(evaluation.getWeightIntonation());
        BigDecimal weightedTone = scoreTone.multiply(evaluation.getWeightTone());
        BigDecimal weightedSpeed = scoreSpeed.multiply(evaluation.getWeightSpeed());
        BigDecimal weightedAccent = scoreAccent.multiply(evaluation.getWeightAccent());

        log.info("========== 가중 점수 계산 ==========");
        log.info("발음 가중 점수: {} × {} = {}", scorePronunciation, evaluation.getWeightPronunciation(), weightedPronunciation);
        log.info("쉼 가중 점수: {} × {} = {}", scorePause, evaluation.getWeightPause(), weightedPause);
        log.info("억양 가중 점수: {} × {} = {}", scoreIntonation, evaluation.getWeightIntonation(), weightedIntonation);
        log.info("톤 가중 점수: {} × {} = {}", scoreTone, evaluation.getWeightTone(), weightedTone);
        log.info("속도 가중 점수: {} × {} = {}", scoreSpeed, evaluation.getWeightSpeed(), weightedSpeed);
        log.info("강세 가중 점수: {} × {} = {}", scoreAccent, evaluation.getWeightAccent(), weightedAccent);

        weightedSum = weightedSum.add(weightedPronunciation);
        weightedSum = weightedSum.add(weightedPause);
        weightedSum = weightedSum.add(weightedIntonation);
        weightedSum = weightedSum.add(weightedTone);
        weightedSum = weightedSum.add(weightedSpeed);
        weightedSum = weightedSum.add(weightedAccent);

        log.info("가중 점수 합계: {}", weightedSum);

        // 가중치 합계가 100이므로 100으로 나눔
        BigDecimal finalScore = weightedSum.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        log.info("최종 가중 평균 점수: {} ÷ 100 = {}", weightedSum, finalScore);
        log.info("=====================================");

        return finalScore;
    }
}
