package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep1;
import org.skala.crewvoice.dto.request.QuestionStep1Request;
import org.skala.crewvoice.dto.response.QuestionStep1DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep1Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.QuestionStep1Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionStep1Service extends BaseQuestionService {

    private final QuestionStep1Repository questionStep1Repository;

    /**
     * STEP1 문제 목록 조회 (페이지네이션)
     *
     * @param pageNumber 페이지 번호 (1-based)
     * @return STEP1 문제 리스트
     */
    public List<QuestionStep1DetailResponse> getStep1Questions(int pageNumber) {
        return getQuestions(pageNumber, questionStep1Repository, QuestionStep1DetailResponse::from);
    }

    /**
     * STEP1 문제 추가 또는 수정
     * questionId가 null이면 추가, 값이 있으면 수정
     *
     * @param request STEP1 문제 요청 DTO
     * @return 생성/수정된 문제 ID
     */
    @Transactional
    public QuestionStep1Response saveOrUpdateQuestion(QuestionStep1Request request) {
        if (request.getQuestionId() == null) {
            // 새로운 문제 추가
            QuestionStep1 newQuestion = new QuestionStep1(
                    request.getQuestionText(),
                    request.getOptionA(),
                    request.getOptionB(),
                    request.getOptionC(),
                    request.getOptionD(),
                    request.getAnswer().toUpperCase(),
                    request.getExplanation()
            );
            QuestionStep1 savedQuestion = questionStep1Repository.save(newQuestion);
            return QuestionStep1Response.of(savedQuestion.getId());
        } else {
            // 기존 문제 수정
            QuestionStep1 existingQuestion = questionStep1Repository.findById(request.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

            existingQuestion.updateQuestion(
                    request.getQuestionText(),
                    request.getOptionA(),
                    request.getOptionB(),
                    request.getOptionC(),
                    request.getOptionD(),
                    request.getAnswer().toUpperCase(),
                    request.getExplanation()
            );

            return QuestionStep1Response.of(existingQuestion.getId());
        }
    }
}
