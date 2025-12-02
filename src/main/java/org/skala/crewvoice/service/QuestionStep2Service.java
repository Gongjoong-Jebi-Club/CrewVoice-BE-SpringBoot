package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.QuestionStep2;
import org.skala.crewvoice.dto.request.QuestionStep2Request;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionStep2Service {

    private final QuestionStep2Repository questionStep2Repository;

    public QuestionStep2Service(QuestionStep2Repository questionStep2Repository) {
        this.questionStep2Repository = questionStep2Repository;
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
}
