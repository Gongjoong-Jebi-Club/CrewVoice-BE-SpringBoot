package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.QuestionStep2;
import org.skala.crewvoice.dto.request.QuestionStep2RandomRequest;
import org.skala.crewvoice.dto.request.QuestionStep2Request;
import org.skala.crewvoice.dto.request.QuestionStep2UpdateRequest;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionResponse;
import org.skala.crewvoice.dto.response.QuestionStep2DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep2RandomResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionStep2Service {

    private final QuestionStep2Repository questionStep2Repository;

    public QuestionStep2Service(QuestionStep2Repository questionStep2Repository) {
        this.questionStep2Repository = questionStep2Repository;
    }

    public List<QuestionResponse> getStep2Questions(int pageNumber) {
        // 페이지는 0부터 시작하므로 pageNumber - 1
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        Page<QuestionStep2> questionsPage = questionStep2Repository.findAll(pageable);

        return questionsPage.getContent()
                .stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    public QuestionStep2DetailResponse getQuestionById(Long questionId) {
        QuestionStep2 question = questionStep2Repository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionStep2DetailResponse.from(question);
    }

    public QuestionStep2RandomResponse getRandomQuestion(QuestionStep2RandomRequest request) {
        boolean isEmpty = request.getQuestionIdList() == null || request.getQuestionIdList().isEmpty();
        List<Long> excludedIds = isEmpty ? List.of() : request.getQuestionIdList();

        QuestionStep2 question = questionStep2Repository.findRandomQuestion(excludedIds, isEmpty)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        // 이전 문제 ID 목록에 새로 선택된 문제 ID 추가
        List<Long> accumulatedQuestions = isEmpty ?
                List.of(question.getId()) :
                java.util.stream.Stream.concat(excludedIds.stream(), java.util.stream.Stream.of(question.getId()))
                        .collect(Collectors.toList());

        return QuestionStep2RandomResponse.of(question, accumulatedQuestions);
    }

    @Transactional
    public QuestionEditResponse addQuestion(QuestionStep2Request request) {
        QuestionStep2 newQuestion = new QuestionStep2(
                request.getSituationDescription(),
                request.getAircraftType(),
                request.getScriptKorean(),
                request.getScriptEnglish(),
                request.getExplanation()
        );

        QuestionStep2 savedQuestion = questionStep2Repository.save(newQuestion);
        return new QuestionEditResponse(savedQuestion.getId());
    }

    @Transactional
    public QuestionEditResponse updateQuestion(Long questionId, QuestionStep2UpdateRequest request) {
        QuestionStep2 question = questionStep2Repository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        question.update(
                request.getSituationDescription(),
                request.getAircraftType(),
                request.getExplanation()
        );

        return new QuestionEditResponse(question.getId());
    }
}
