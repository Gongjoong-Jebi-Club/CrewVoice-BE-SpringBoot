package org.skala.crewvoice.service;

import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.EvaluationStep2;
import org.skala.crewvoice.domain.EvaluationStep3;
import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.SubmissionStep1;
import org.skala.crewvoice.domain.SubmissionStep2;
import org.skala.crewvoice.domain.SubmissionStep3;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.response.MockExamDetailResponse;
import org.skala.crewvoice.dto.response.MockExamResultResponse;
import org.skala.crewvoice.dto.response.MockExamScoresResponse;
import org.skala.crewvoice.dto.response.Step1QuestionResultResponse;
import org.skala.crewvoice.dto.response.Step1ResultResponse;
import org.skala.crewvoice.dto.response.Step2ResultResponse;
import org.skala.crewvoice.dto.response.Step3PartResultResponse;
import org.skala.crewvoice.dto.response.Step3ResultResponse;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.repository.EvaluationStep2Repository;
import org.skala.crewvoice.repository.EvaluationStep3Repository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.SubmissionStep1Repository;
import org.skala.crewvoice.repository.SubmissionStep2Repository;
import org.skala.crewvoice.repository.SubmissionStep3Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.skala.crewvoice.domain.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MockExamResultService {

    private final FinalGradeRepository finalGradeRepository;
    private final SubmissionStep1Repository submissionStep1Repository;
    private final SubmissionStep2Repository submissionStep2Repository;
    private final EvaluationStep2Repository evaluationStep2Repository;
    private final SubmissionStep3Repository submissionStep3Repository;
    private final EvaluationStep3Repository evaluationStep3Repository;

    public MockExamResultService(FinalGradeRepository finalGradeRepository,
                                 SubmissionStep1Repository submissionStep1Repository,
                                 SubmissionStep2Repository submissionStep2Repository,
                                 EvaluationStep2Repository evaluationStep2Repository,
                                 SubmissionStep3Repository submissionStep3Repository,
                                 EvaluationStep3Repository evaluationStep3Repository) {
        this.finalGradeRepository = finalGradeRepository;
        this.submissionStep1Repository = submissionStep1Repository;
        this.submissionStep2Repository = submissionStep2Repository;
        this.evaluationStep2Repository = evaluationStep2Repository;
        this.submissionStep3Repository = submissionStep3Repository;
        this.evaluationStep3Repository = evaluationStep3Repository;
    }

    /**
     * 현재 로그인한 사용자의 모의고사 결과를 최신 응시순으로 조회
     *
     * @param user 현재 로그인한 사용자
     * @return 모의고사 결과 리스트 (최신순)
     */
    public List<MockExamResultResponse> getMyMockExamResults(User user) {
        List<FinalGrade> finalGrades = finalGradeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return finalGrades.stream()
                .map(MockExamResultResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 평가의 상세 점수 조회
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return 모의고사 상세 점수
     */
    public MockExamDetailResponse getMockExamDetail(User user, Long evaluationId) {
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(user.getId(), evaluationId);

        if (finalGrade == null) {
            throw new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        return MockExamDetailResponse.from(finalGrade);
    }

    /**
     * 특정 모의고사의 점수 조회
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return 모의고사 점수
     */
    public MockExamScoresResponse getMockExamScores(User user, Long evaluationId) {
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(user.getId(), evaluationId);

        if (finalGrade == null) {
            throw new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        return MockExamScoresResponse.from(finalGrade);
    }

    /**
     * STEP2 평가 결과 상세 조회
     * 한국어와 영어 평가 점수의 평균을 계산하여 반환
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return Step2 평가 결과
     */
    public Step2ResultResponse getStep2Result(User user, Long evaluationId) {
        log.info("Step2 결과 조회 시작 - userId: {}, evaluationId: {}", user.getId(), evaluationId);

        // 1. 제출 기록 조회
        List<SubmissionStep2> submissions = submissionStep2Repository
                .findByUserIdAndEvaluationId(user.getId(), evaluationId);

        if (submissions.isEmpty()) {
            log.warn("제출 기록을 찾을 수 없음 - userId: {}, evaluationId: {} (기본값 반환)",
                    user.getId(), evaluationId);
            return Step2ResultResponse.empty(user.getId());
        }

        // 첫 번째 제출 기록 사용 (보통 1개만 존재)
        SubmissionStep2 submission = submissions.get(0);
        log.info("제출 기록 조회 성공 - submissionId: {}, 조회된 개수: {}", submission.getSubmissionId(), submissions.size());

        // 2. 평가 결과 조회 (한국어, 영어 2개)
        List<EvaluationStep2> evaluations = evaluationStep2Repository
                .findBySubmissionId(submission.getSubmissionId());

        log.info("평가 결과 조회 완료 - 조회된 개수: {}", evaluations.size());

        if (evaluations.size() != 2) {
            log.warn("평가 결과가 2개가 아님 - submissionId: {}, 조회된 개수: {} (기본값 반환)",
                    submission.getSubmissionId(), evaluations.size());
            return Step2ResultResponse.empty(user.getId());
        }

        // 3. 한국어와 영어 평가 구분
        EvaluationStep2 koreanEval = evaluations.stream()
                .filter(e -> e.getLanguage() == org.skala.crewvoice.domain.Language.KOREAN)
                .findFirst()
                .orElse(null);

        EvaluationStep2 englishEval = evaluations.stream()
                .filter(e -> e.getLanguage() == org.skala.crewvoice.domain.Language.ENGLISH)
                .findFirst()
                .orElse(null);

        if (koreanEval == null || englishEval == null) {
            log.warn("한국어 또는 영어 평가 결과 누락 - submissionId: {} (기본값 반환)",
                    submission.getSubmissionId());
            return Step2ResultResponse.empty(user.getId());
        }

        log.info("한국어/영어 평가 구분 완료");

        // 4. 응답 생성
        return Step2ResultResponse.fromEvaluations(user.getId(), koreanEval, englishEval);
    }

    /**
     * STEP3 평가 결과 상세 조회
     * 여러 파트의 한국어/영어 평가 점수를 조회하여 반환
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return Step3 평가 결과
     */
    public Step3ResultResponse getStep3Result(User user, Long evaluationId) {
        log.info("Step3 결과 조회 시작 - userId: {}, evaluationId: {}", user.getId(), evaluationId);

        // 1. 제출 기록 조회 (part_order 순으로 정렬)
        List<SubmissionStep3> submissions = submissionStep3Repository
                .findByUserIdAndEvaluationIdOrderByPartOrder(user.getId(), evaluationId);

        if (submissions.isEmpty()) {
            log.warn("제출 기록을 찾을 수 없음 - userId: {}, evaluationId: {} (빈 리스트 반환)",
                    user.getId(), evaluationId);
            return Step3ResultResponse.empty(user.getId());
        }

        log.info("제출 기록 조회 성공 - 조회된 파트 개수: {}", submissions.size());

        // 2. 각 제출 기록에 대한 평가 결과 조회 및 DTO 변환
        List<Step3PartResultResponse> parts = new ArrayList<>();

        for (SubmissionStep3 submission : submissions) {
            // 해당 submission의 평가 결과 조회 (한국어, 영어 2개)
            List<EvaluationStep3> evaluations = evaluationStep3Repository
                    .findBySubmissionId(submission.getSubmissionId());

            log.info("Part {} 평가 결과 조회 완료 - 조회된 개수: {}", submission.getPartOrder(), evaluations.size());

            if (evaluations.size() != 2) {
                log.warn("평가 결과가 2개가 아님 - submissionId: {}, partOrder: {}, 조회된 개수: {} (해당 파트 스킵)",
                        submission.getSubmissionId(), submission.getPartOrder(), evaluations.size());
                continue; // 이 파트는 건너뛰고 다음 파트 처리
            }

            // 한국어와 영어 평가 구분
            EvaluationStep3 koreanEval = evaluations.stream()
                    .filter(e -> e.getLanguage() == Language.KOREAN)
                    .findFirst()
                    .orElse(null);

            EvaluationStep3 englishEval = evaluations.stream()
                    .filter(e -> e.getLanguage() == Language.ENGLISH)
                    .findFirst()
                    .orElse(null);

            if (koreanEval == null || englishEval == null) {
                log.warn("한국어 또는 영어 평가 결과 누락 - submissionId: {}, partOrder: {} (해당 파트 스킵)",
                        submission.getSubmissionId(), submission.getPartOrder());
                continue; // 이 파트는 건너뛰고 다음 파트 처리
            }

            // Part 결과 생성 및 리스트에 추가
            Step3PartResultResponse partResult = Step3PartResultResponse.fromEvaluations(
                    submission.getPartOrder(), koreanEval, englishEval);
            parts.add(partResult);

            log.info("Part {} 한국어/영어 평가 구분 완료", submission.getPartOrder());
        }

        log.info("Step3 전체 결과 조회 완료 - 총 파트 개수: {}", parts.size());

        // 3. 응답 생성
        return Step3ResultResponse.of(user.getId(), parts);
    }

    /**
     * STEP1 오답노트 조회
     * 문제별 제출 기록과 정답 여부를 조회하여 반환
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return Step1 오답노트 결과
     */
    public Step1ResultResponse getStep1Result(User user, Long evaluationId) {
        log.info("Step1 오답노트 조회 시작 - userId: {}, evaluationId: {}", user.getId(), evaluationId);

        // 1. 제출 기록 조회 (question_order 순으로 정렬, question도 함께 fetch)
        List<SubmissionStep1> submissions = submissionStep1Repository
                .findByUserIdAndEvaluationIdOrderByQuestionOrder(user.getId(), evaluationId);

        if (submissions.isEmpty()) {
            log.error("제출 기록을 찾을 수 없음 - userId: {}, evaluationId: {}", user.getId(), evaluationId);
            throw new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        log.info("제출 기록 조회 성공 - 조회된 문제 개수: {}", submissions.size());

        // 2. 각 제출 기록을 DTO로 변환
        List<Step1QuestionResultResponse> questions = submissions.stream()
                .map(Step1QuestionResultResponse::from)
                .collect(Collectors.toList());

        log.info("Step1 오답노트 조회 완료 - 총 문제 개수: {}", questions.size());

        // 3. 응답 생성
        return Step1ResultResponse.of(questions);
    }
}

