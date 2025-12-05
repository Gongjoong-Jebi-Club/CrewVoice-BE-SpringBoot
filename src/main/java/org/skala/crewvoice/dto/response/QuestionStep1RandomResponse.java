package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.QuestionStep1;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStep1RandomResponse {
    private List<Long> questionIdList;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    public static QuestionStep1RandomResponse of(List<Long> questionIdList, QuestionStep1 question) {
        return new QuestionStep1RandomResponse(
                questionIdList,
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        );
    }
}
