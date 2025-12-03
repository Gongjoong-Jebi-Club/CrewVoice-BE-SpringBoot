package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.QuestionStep3;
import org.skala.crewvoice.dto.request.QuestionStep3RandomRequest;
import org.skala.crewvoice.dto.response.QuestionStep3ListResponse;
import org.skala.crewvoice.dto.response.QuestionStep3RandomResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.QuestionStep3Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionStep3Service {

    private final QuestionStep3Repository questionStep3Repository;

    public QuestionStep3Service(QuestionStep3Repository questionStep3Repository) {
        this.questionStep3Repository = questionStep3Repository;
    }

    public List<QuestionStep3ListResponse> getStep3Questions(int pageNumber) {
        // 페이지는 0부터 시작하므로 pageNumber - 1
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        Page<QuestionStep3> questionsPage = questionStep3Repository.findAll(pageable);

        return questionsPage.getContent()
                .stream()
                .map(QuestionStep3ListResponse::from)
                .collect(Collectors.toList());
    }

    public QuestionStep3RandomResponse getRandomQuestion(QuestionStep3RandomRequest request) {
        List<Long> questionIdList = request.getQuestionIdList();
        boolean isEmpty = questionIdList == null || questionIdList.isEmpty();

        // questionIdList가 null이면 빈 리스트로 처리
        List<Long> excludedIds = isEmpty ? List.of() : questionIdList;

        QuestionStep3 question = questionStep3Repository.findRandomQuestion(excludedIds, isEmpty)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        // 새로운 questionIdList 생성 (기존 리스트 + 현재 조회된 questionId)
        List<Long> newQuestionIdList = new ArrayList<>(excludedIds);
        newQuestionIdList.add(question.getId());

        return QuestionStep3RandomResponse.of(newQuestionIdList, question);
    }
}
