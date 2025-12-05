package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.QuestionStep1;
import org.skala.crewvoice.domain.SubmissionStep1;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.request.SubmissionStep1Request;
import org.skala.crewvoice.dto.response.SubmissionStep1Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.QuestionStep1Repository;
import org.skala.crewvoice.repository.SubmissionStep1Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionStep1Service {

    private final SubmissionStep1Repository submissionStep1Repository;
    private final QuestionStep1Repository questionStep1Repository;
    private final EvaluationRepository evaluationRepository;

    /**
     * STEP1 답안 제출 및 채점
     *
     * @param user    인증된 사용자
     * @param request 답안 제출 요청 (evaluationId, questionId, questionOrder, submission)
     * @return 생성된 submission ID
     */
    @Transactional
    public SubmissionStep1Response submitAnswer(User user, SubmissionStep1Request request) {
        // Evaluation 조회
        Evaluation evaluation = evaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));

        // Question 조회
        QuestionStep1 question = questionStep1Repository.findById(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        // 답안을 대문자로 변환
        String userAnswer = request.getUserAnswer().toUpperCase();

        // 정답 여부 확인 (자동 채점)
        boolean isCorrect = userAnswer.equals(question.getCorrectAnswer());

        // Submission 엔티티 생성 및 저장
        SubmissionStep1 submission = new SubmissionStep1(
                user,
                evaluation,
                question,
                request.getQuestionOrder(),
                userAnswer,
                isCorrect
        );

        SubmissionStep1 savedSubmission = submissionStep1Repository.save(submission);

        return SubmissionStep1Response.of(savedSubmission.getId());
    }
}
