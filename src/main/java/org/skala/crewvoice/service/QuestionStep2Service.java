package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.QuestionStep2;
import org.skala.crewvoice.dto.request.QuestionStep2Request;
import org.skala.crewvoice.dto.request.QuestionStep2UpdateRequest;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionStep2ListResponse;
import org.skala.crewvoice.dto.response.QuestionStep2DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep2RandomResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuestionStep2Service extends BaseQuestionService {

    private final QuestionStep2Repository questionStep2Repository;

    public QuestionStep2Service(QuestionStep2Repository questionStep2Repository) {
        this.questionStep2Repository = questionStep2Repository;
    }

    /**
     * STEP2 문제 목록 조회 (ACTIVE 상태만, 페이지네이션)
     *
     * @param pageNumber 페이지 번호 (1-based)
     * @return STEP2 문제 리스트
     */
    public List<QuestionStep2ListResponse> getStep2Questions(int pageNumber) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageNumber - 1, 10);
        org.springframework.data.domain.Page<QuestionStep2> questionsPage =
                questionStep2Repository.findByStatus("ACTIVE", pageable);

        return questionsPage.getContent()
                .stream()
                .map(QuestionStep2ListResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    public QuestionStep2DetailResponse getQuestionById(Long questionId) {
        return getQuestionById(questionId, questionStep2Repository, QuestionStep2DetailResponse::from);
    }

    public QuestionStep2RandomResponse getRandomQuestion() {
        QuestionStep2 question = questionStep2Repository.findRandomQuestion(List.of(), true)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionStep2RandomResponse.from(question);
    }

    @Transactional
    public QuestionEditResponse saveOrUpdateQuestion(QuestionStep2Request request) {
        return saveOrUpdateQuestion(
                request.getQuestionId(),
                questionStep2Repository,
                () -> new QuestionStep2(
                        request.getSituationDescription(),
                        request.getAircraftType(),
                        request.getScriptKorean(),
                        request.getScriptEnglish(),
                        request.getExplanation()
                ),
                question -> question.update(
                        request.getSituationDescription(),
                        request.getAircraftType(),
                        request.getScriptKorean(),
                        request.getScriptEnglish(),
                        request.getExplanation()
                )
        );
    }
}
