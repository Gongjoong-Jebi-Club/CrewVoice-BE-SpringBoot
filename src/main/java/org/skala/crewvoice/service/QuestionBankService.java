package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.skala.crewvoice.domain.QuestionStep1;
import org.skala.crewvoice.domain.QuestionStep2;
import org.skala.crewvoice.domain.QuestionStep3;
import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse;
import org.skala.crewvoice.dto.response.QuestionDeleteResponse;
import org.skala.crewvoice.dto.response.QuestionStep2Response;
import org.skala.crewvoice.dto.response.QuestionStep3Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.skala.crewvoice.repository.QuestionStep1Repository;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.skala.crewvoice.repository.QuestionStep3Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse.StepSummary;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문제 은행 관리 서비스
 * - STEP2, STEP3 문제 조회/추가/수정/삭제
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionBankService {

    private final QuestionStep1Repository questionStep1Repository;
    private final QuestionStep2Repository questionStep2Repository;
    private final QuestionStep3Repository questionStep3Repository;

    /**
     * STEP2 또는 STEP3 문제 전체 리스트 조회
     * @param step "step2" 또는 "step3"
     * @return 문제 리스트 (DTO)
     */
    public List<?> getQuestionsByStep(String step) {
        log.info("문제 리스트 조회 시작: step={}", step);

        if ("step2".equalsIgnoreCase(step)) {
            List<QuestionStep2Response> responses = questionStep2Repository
                    .findAllByOrderByCreatedAtAsc()
                    .stream()
                    .map(QuestionStep2Response::from)
                    .collect(Collectors.toList());

            log.info("STEP2 문제 조회 완료: 총 {}개", responses.size());
            return responses;

        } else if ("step3".equalsIgnoreCase(step)) {
            List<QuestionStep3Response> responses = questionStep3Repository
                    .findAllByOrderByCreatedAtAsc()
                    .stream()
                    .map(QuestionStep3Response::from)
                    .collect(Collectors.toList());

            log.info("STEP3 문제 조회 완료: 총 {}개", responses.size());
            return responses;

        } else {
            log.error("유효하지 않은 step 값: {}", step);
            throw new InvalidRequestException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    /**
     * STEP별 문제 개수와 최종 수정 일자 조회
     *
     * 관리자가 문제은행 페이지에 접속 시 호출되는 API
     * STEP1, STEP2, STEP3 각각의 총 문제 수와 최종 수정 일시를 반환
     *
     * @return STEP별 문제 통계 정보
     */
    public QuestionBankSummaryResponse getQuestionBankSummary() {
        log.info("문제은행 기본 정보 조회 시작");

        List<StepSummary> summaries = new ArrayList<>();

        // STEP1 통계
        long step1Count = questionStep1Repository.count();
        OffsetDateTime step1UpdatedAt = questionStep1Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step1", (int) step1Count, step1UpdatedAt));
        log.debug("STEP1 통계 - 문제 수: {}, 최종 수정: {}", step1Count, step1UpdatedAt);

        // STEP2 통계
        long step2Count = questionStep2Repository.count();
        OffsetDateTime step2UpdatedAt = questionStep2Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step2", (int) step2Count, step2UpdatedAt));
        log.debug("STEP2 통계 - 문제 수: {}, 최종 수정: {}", step2Count, step2UpdatedAt);

        // STEP3 통계
        long step3Count = questionStep3Repository.count();
        OffsetDateTime step3UpdatedAt = questionStep3Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step3", (int) step3Count, step3UpdatedAt));
        log.debug("STEP3 통계 - 문제 수: {}, 최종 수정: {}", step3Count, step3UpdatedAt);

        log.info("문제은행 기본 정보 조회 완료 - 총 {}개 STEP", summaries.size());

        return QuestionBankSummaryResponse.builder()
                .data(summaries)
                .build();
    }

    /**
     * ✅ 특정 문제 삭제 (Soft Delete) (API 17)
     *
     * status 컬럼을 'DELETED'로 변경하여 논리적 삭제 처리
     * 오답노트 등에서 참조 오류를 방지하기 위해 soft delete 사용
     *
     * @param step STEP 구분 (step1, step2, step3)
     * @param questionId 삭제할 문제 ID
     * @return 삭제된 문제 ID
     */
    @Transactional
    public QuestionDeleteResponse deleteQuestion(String step, Long questionId) {
        log.info("[API 17] 문제 삭제 요청 - step: {}, questionId: {}", step, questionId);

        // STEP 검증
        if (!isValidStep(step)) {
            log.error("유효하지 않은 STEP - step: {}", step);
            throw new InvalidRequestException(ErrorCode.INVALID_QUESTION_STEP);
        }

        // STEP별 삭제 처리
        switch (step.toLowerCase()) {
            case "step1":
                deleteStep1Question(questionId);
                break;
            case "step2":
                deleteStep2Question(questionId);
                break;
            case "step3":
                deleteStep3Question(questionId);
                break;
            default:
                log.error("지원하지 않는 STEP - step: {}", step);
                throw new InvalidRequestException(ErrorCode.INVALID_QUESTION_STEP);
        }

        log.info("[API 17] 문제 삭제 완료 - step: {}, questionId: {}", step, questionId);

        return QuestionDeleteResponse.of(questionId);
    }

    /**
     * STEP1 문제 삭제 (Soft Delete)
     */
    private void deleteStep1Question(Long questionId) {
        QuestionStep1 question = questionStep1Repository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("STEP1 문제를 찾을 수 없음 - questionId: {}", questionId);
                    return new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND);
                });

        // 이미 삭제된 문제인지 확인
        if ("DELETED".equals(question.getStatus())) {
            log.warn("이미 삭제된 STEP1 문제 - questionId: {}", questionId);
            throw new InvalidRequestException(ErrorCode.ALREADY_DELETED_QUESTION);
        }

        // Soft Delete 처리
        question.delete();
        log.info("STEP1 문제 삭제 완료 - questionId: {}", questionId);
    }

    /**
     * STEP2 문제 삭제 (Soft Delete)
     */
    private void deleteStep2Question(Long questionId) {
        QuestionStep2 question = questionStep2Repository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("STEP2 문제를 찾을 수 없음 - questionId: {}", questionId);
                    return new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND);
                });

        // 이미 삭제된 문제인지 확인
        if ("DELETED".equals(question.getStatus())) {
            log.warn("이미 삭제된 STEP2 문제 - questionId: {}", questionId);
            throw new InvalidRequestException(ErrorCode.ALREADY_DELETED_QUESTION);
        }

        // Soft Delete 처리
        question.delete();
        log.info("STEP2 문제 삭제 완료 - questionId: {}", questionId);
    }

    /**
     * STEP3 문제 삭제 (Soft Delete)
     */
    private void deleteStep3Question(Long questionId) {
        QuestionStep3 question = questionStep3Repository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("STEP3 문제를 찾을 수 없음 - questionId: {}", questionId);
                    return new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND);
                });

        // 이미 삭제된 문제인지 확인
        if ("DELETED".equals(question.getStatus())) {
            log.warn("이미 삭제된 STEP3 문제 - questionId: {}", questionId);
            throw new InvalidRequestException(ErrorCode.ALREADY_DELETED_QUESTION);
        }

        // Soft Delete 처리
        question.delete();
        log.info("STEP3 문제 삭제 완료 - questionId: {}", questionId);
    }

    /**
     * STEP 유효성 검증
     */
    private boolean isValidStep(String step) {
        if (step == null) {
            return false;
        }
        String lowerStep = step.toLowerCase();
        return lowerStep.equals("step1") || lowerStep.equals("step2") || lowerStep.equals("step3");
    }
}