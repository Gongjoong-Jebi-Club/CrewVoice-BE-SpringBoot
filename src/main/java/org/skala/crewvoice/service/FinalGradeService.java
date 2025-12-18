package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.repository.EvaluationStep3Repository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.SubmissionStep1Repository;
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
        log.info("🔵 [FinalGradeService.updateStep2Score] 메서드 호출됨 - evaluationStep2Id: {}",
                evaluationStep2 != null ? evaluationStep2.getId() : "NULL");

        SubmissionStep2 submission = evaluationStep2.getSubmission();
        Long userId = submission.getUser().getId();
        Long evaluationId = submission.getEvaluation().getEvaluationId();
        Language language = evaluationStep2.getLanguage();

        log.info("========== Step2 점수 업데이트 시작 ==========");
        log.info("userId: {}, evaluationId: {}, language: {}, languageCode: {}",
                userId, evaluationId, language, language != null ? language.getCode() : "NULL");
        log.info("evaluationStep2Id: {}, submissionId: {}",
                evaluationStep2.getId(), submission.getSubmissionId());

        // FinalGrade 조회
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(userId, evaluationId);

        if (finalGrade == null) {
            log.error("❌ FinalGrade 레코드가 존재하지 않습니다 - userId: {}, evaluationId: {}", userId, evaluationId);
            return;
        }

        log.info("FinalGrade 조회 완료 - gradeId: {}, 현재 step2KorScore: {}, step2EngScore: {}",
                finalGrade.getGradeId(), finalGrade.getStep2KorScore(), finalGrade.getStep2EngScore());

        // evaluations_step2의 overall_score를 그대로 사용
        BigDecimal overallScore = evaluationStep2.getOverallScore();

        if (overallScore == null) {
            log.error("❌ overall_score가 null입니다 - evaluationStep2Id: {}", evaluationStep2.getId());
            return;
        }

        log.info("evaluations_step2의 overall_score: {}", overallScore);

        // 언어에 따라 업데이트 (Race Condition 방지를 위해 @Modifying 쿼리 사용)
        int updatedRows = 0;
        if (language == Language.KOREAN) {
            log.info("✓ 한국어 점수 업데이트 진행 - 변경 전: {}, 변경 후: {}",
                    finalGrade.getStep2KorScore(), overallScore);
            updatedRows = finalGradeRepository.updateStep2KorScore(userId, evaluationId, overallScore);
            log.info("✓ Step2 한국어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}, updatedRows: {}",
                    userId, evaluationId, overallScore, updatedRows);
        } else if (language == Language.ENGLISH) {
            log.info("✓ 영어 점수 업데이트 진행 - 변경 전: {}, 변경 후: {}",
                    finalGrade.getStep2EngScore(), overallScore);
            updatedRows = finalGradeRepository.updateStep2EngScore(userId, evaluationId, overallScore);
            log.info("✓ Step2 영어 점수 업데이트 완료 - userId: {}, evaluationId: {}, score: {}, updatedRows: {}",
                    userId, evaluationId, overallScore, updatedRows);
        } else {
            log.error("❌ 알 수 없는 언어 값 - language: {}", language);
        }

        if (updatedRows == 0) {
            log.error("❌ FinalGrade 업데이트 실패 - 업데이트된 행이 없음 (userId: {}, evaluationId: {})",
                    userId, evaluationId);
        } else {
            log.info("FinalGrade 저장 완료 - userId: {}, evaluationId: {}, language: {}, updatedRows: {}",
                    userId, evaluationId, language.getCode(), updatedRows);
        }
        log.info("========== Step2 점수 업데이트 종료 ==========");
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
        log.info("🔵 [FinalGradeService.updateStep3Score] 메서드 호출됨 - evaluationStep3Id: {}",
                evaluationStep3 != null ? evaluationStep3.getId() : "NULL");

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

        // 3개 파트의 overall_score 평균 계산
        BigDecimal totalScore = BigDecimal.ZERO;

        for (EvaluationStep3 eval : completedEvaluations) {
            BigDecimal overallScore = eval.getOverallScore();

            if (overallScore == null) {
                log.error("❌ Part {}의 overall_score가 null입니다 - evaluationStep3Id: {}",
                        eval.getSubmission().getPartOrder(), eval.getId());
                return;
            }

            totalScore = totalScore.add(overallScore);
            log.info("Part {} overall_score: {}", eval.getSubmission().getPartOrder(), overallScore);
        }

        BigDecimal averageScore = totalScore.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
        log.info("Step3 3개 파트 overall_score 평균: {} (합계: {} ÷ 3)", averageScore, totalScore);

        // 언어에 따라 업데이트 (Race Condition 방지를 위해 @Modifying 쿼리 사용)
        int updatedRows = 0;
        if (language == Language.KOREAN) {
            updatedRows = finalGradeRepository.updateStep3KorScore(userId, evaluationId, averageScore);
            log.info("Step3 한국어 점수 업데이트 완료 - userId: {}, evaluationId: {}, avgScore: {}, updatedRows: {}",
                    userId, evaluationId, averageScore, updatedRows);
        } else if (language == Language.ENGLISH) {
            updatedRows = finalGradeRepository.updateStep3EngScore(userId, evaluationId, averageScore);
            log.info("Step3 영어 점수 업데이트 완료 - userId: {}, evaluationId: {}, avgScore: {}, updatedRows: {}",
                    userId, evaluationId, averageScore, updatedRows);
        }

        if (updatedRows == 0) {
            log.error("❌ FinalGrade Step3 업데이트 실패 - 업데이트된 행이 없음 (userId: {}, evaluationId: {})",
                    userId, evaluationId);
        }
    }
}
