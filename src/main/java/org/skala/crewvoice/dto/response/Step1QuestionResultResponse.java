package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.SubmissionStep1;

@Getter
public class Step1QuestionResultResponse {

    private final Integer questionOrder;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String userAnswer;
    private final String correctAnswer;
    private final String explanation;

    private Step1QuestionResultResponse(Integer questionOrder, String questionText,
                                        String optionA, String optionB, String optionC,
                                        String optionD, String userAnswer,
                                        String correctAnswer, String explanation) {
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.userAnswer = userAnswer.toUpperCase();
        this.correctAnswer = correctAnswer.toUpperCase();
        this.explanation = explanation;
    }

    /**
     * SubmissionStep1 엔티티로부터 Step1QuestionResultResponse 생성
     *
     * @param submission Step1 제출 기록
     * @return Step1QuestionResultResponse
     */
    public static Step1QuestionResultResponse from(SubmissionStep1 submission) {
        return new Step1QuestionResultResponse(
                submission.getQuestionOrder(),
                submission.getQuestion().getQuestionText(),
                submission.getQuestion().getOptionA(),
                submission.getQuestion().getOptionB(),
                submission.getQuestion().getOptionC(),
                submission.getQuestion().getOptionD(),
                submission.getUserAnswer(),
                submission.getQuestion().getCorrectAnswer(),
                submission.getQuestion().getExplanation()
        );
    }
}
