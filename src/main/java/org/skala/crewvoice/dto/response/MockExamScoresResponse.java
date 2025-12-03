package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.FinalGrade;

import java.math.BigDecimal;

@Getter
public class MockExamScoresResponse {

    private final BigDecimal totalScore;
    private final BigDecimal step1Score;
    private final BigDecimal step2Score;
    private final BigDecimal step3Score;

    private MockExamScoresResponse(BigDecimal totalScore, BigDecimal step1Score, BigDecimal step2Score, BigDecimal step3Score) {
        this.totalScore = totalScore;
        this.step1Score = step1Score;
        this.step2Score = step2Score;
        this.step3Score = step3Score;
    }

    public static MockExamScoresResponse from(FinalGrade finalGrade) {
        return new MockExamScoresResponse(
                finalGrade.getTotalScore(),
                finalGrade.getStep1Score(),
                finalGrade.getStep2Score(),
                finalGrade.getStep3Score()
        );
    }
}
