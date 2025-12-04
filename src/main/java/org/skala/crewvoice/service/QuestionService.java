package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.Question;
import org.skala.crewvoice.dto.request.QuestionRequest;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionResponse> getStep2Questions(int pageNumber) {
        // 페이지는 0부터 시작하므로 pageNumber - 1
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        Page<Question> questionsPage = questionRepository.findByStep(2, pageable);

        return questionsPage.getContent()
                .stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionEditResponse upsertStep2Question(QuestionRequest request) {
        // questionId가 있으면 수정, 없으면 추가
        if (request.getQuestionId() != null) {
            // 수정
            Question question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

            question.update(request.getSituationDescription(), request.getAircraftType(), request.getExplanation());
            return new QuestionEditResponse(question.getId());
        } else {
            // 추가
            Question newQuestion = new Question(
                    request.getSituationDescription(),
                    request.getAircraftType(),
                    request.getExplanation(),
                    2  // STEP 2
            );
            Question savedQuestion = questionRepository.save(newQuestion);
            return new QuestionEditResponse(savedQuestion.getId());
        }
    }
}
