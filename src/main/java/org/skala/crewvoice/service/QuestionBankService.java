package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse;
import org.skala.crewvoice.dto.response.QuestionStep2Response;
import org.skala.crewvoice.dto.response.QuestionStep3Response;
import org.skala.crewvoice.exception.ErrorCode;
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
}