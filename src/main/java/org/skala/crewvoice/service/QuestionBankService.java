package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.QuestionStep2Response;
import org.skala.crewvoice.dto.response.QuestionStep3Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.skala.crewvoice.repository.QuestionStep3Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}