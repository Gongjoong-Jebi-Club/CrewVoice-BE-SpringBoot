package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.skala.crewvoice.domain.QuestionStep1;
import org.skala.crewvoice.domain.QuestionStep2;
import org.skala.crewvoice.domain.QuestionStep3;
import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse;
import org.skala.crewvoice.dto.response.QuestionDeleteResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.skala.crewvoice.repository.QuestionStep1Repository;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.skala.crewvoice.repository.QuestionStep3Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse.StepSummary;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
     * STEP별 문제 개수와 최종 수정 일자 조회
     *
     * 관리자가 문제은행 페이지에 접속 시 호출되는 API
     * STEP1, STEP2, STEP3 각각의 ACTIVE 상태 문제 수와 최종 수정 일시를 반환
     *
     * @return STEP별 문제 통계 정보 (ACTIVE 상태만)
     */
    public QuestionBankSummaryResponse getQuestionBankSummary() {
        log.info("문제은행 기본 정보 조회 시작 (ACTIVE 상태만)");

        List<StepSummary> summaries = new ArrayList<>();

        // STEP1 통계 (ACTIVE만)
        long step1Count = questionStep1Repository.countByStatus("ACTIVE");
        OffsetDateTime step1UpdatedAt = questionStep1Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step1", (int) step1Count, step1UpdatedAt));
        log.debug("STEP1 통계 - ACTIVE 문제 수: {}, 최종 수정: {}", step1Count, step1UpdatedAt);

        // STEP2 통계 (ACTIVE만)
        long step2Count = questionStep2Repository.countByStatus("ACTIVE");
        OffsetDateTime step2UpdatedAt = questionStep2Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step2", (int) step2Count, step2UpdatedAt));
        log.debug("STEP2 통계 - ACTIVE 문제 수: {}, 최종 수정: {}", step2Count, step2UpdatedAt);

        // STEP3 통계 (ACTIVE만)
        long step3Count = questionStep3Repository.countByStatus("ACTIVE");
        OffsetDateTime step3UpdatedAt = questionStep3Repository.findLatestUpdatedAt();
        summaries.add(StepSummary.of("step3", (int) step3Count, step3UpdatedAt));
        log.debug("STEP3 통계 - ACTIVE 문제 수: {}, 최종 수정: {}", step3Count, step3UpdatedAt);

        log.info("문제은행 기본 정보 조회 완료 - 총 {}개 STEP (ACTIVE 상태만)", summaries.size());

        return QuestionBankSummaryResponse.builder()
                .data(summaries)
                .build();
    }

    /**
     * ✅ 특정 문제 삭제 (Soft Delete) (API 17) - 리팩토링 버전
     *
     * status 컬럼을 'INACTIVE'로 변경하여 논리적 삭제 처리
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

        // STEP별 삭제 처리 - 통합 버전
        String stepUpper = step.toUpperCase();

        switch (stepUpper) {
            case "STEP1":
                deleteQuestionByStep(questionId, questionStep1Repository, stepUpper,
                        QuestionStep1::getStatus, QuestionStep1::delete);
                break;
            case "STEP2":
                deleteQuestionByStep(questionId, questionStep2Repository, stepUpper,
                        QuestionStep2::getStatus, QuestionStep2::delete);
                break;
            case "STEP3":
                deleteQuestionByStep(questionId, questionStep3Repository, stepUpper,
                        QuestionStep3::getStatus, QuestionStep3::delete);
                break;
            default:
                log.error("지원하지 않는 STEP - step: {}", step);
                throw new InvalidRequestException(ErrorCode.INVALID_QUESTION_STEP);
        }

        log.info("[API 17] 문제 삭제 완료 - step: {}, questionId: {}", step, questionId);

        return QuestionDeleteResponse.of(questionId);
    }

    /**
     * 문제 삭제 공통 로직 (Generic)
     *
     * @param questionId 삭제할 문제 ID
     * @param repository 해당 STEP의 Repository
     * @param stepName STEP 이름 (로그용)
     * @param statusGetter Entity에서 status를 가져오는 메서드 참조
     * @param deleteMethod Entity의 delete() 메서드 참조
     * @param <T> Entity 타입 (QuestionStep1, QuestionStep2, QuestionStep3)
     */
    private <T> void deleteQuestionByStep(
            Long questionId,
            JpaRepository<T, Long> repository,
            String stepName,
            Function<T, String> statusGetter,
            Consumer<T> deleteMethod
    ) {
        T question = repository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("{} 문제를 찾을 수 없음 - questionId: {}", stepName, questionId);
                    return new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND);
                });

        // 이미 삭제된 문제인지 확인
        if ("INACTIVE".equals(statusGetter.apply(question))) {
            log.warn("이미 삭제된 {} 문제 - questionId: {}", stepName, questionId);
            throw new InvalidRequestException(ErrorCode.ALREADY_DELETED_QUESTION);
        }

        // Soft Delete 처리
        deleteMethod.accept(question);
        log.info("{} 문제 삭제 완료 - questionId: {}", stepName, questionId);
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