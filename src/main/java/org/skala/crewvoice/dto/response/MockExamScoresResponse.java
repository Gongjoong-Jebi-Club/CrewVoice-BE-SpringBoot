package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.FinalGrade;

import java.math.BigDecimal;

@Getter
public class MockExamScoresResponse {

    private final BigDecimal step1Score;
    private final BigDecimal step2KorScore;
    private final BigDecimal step2EngScore;
    private final BigDecimal step3KorAvgScore;
    private final BigDecimal step3EngAvgScore;

    private MockExamScoresResponse(BigDecimal step1Score, BigDecimal step2KorScore, BigDecimal step2EngScore,
                                   BigDecimal step3KorAvgScore, BigDecimal step3EngAvgScore) {
        this.step1Score = step1Score;
        this.step2KorScore = step2KorScore;
        this.step2EngScore = step2EngScore;
        this.step3KorAvgScore = step3KorAvgScore;
        this.step3EngAvgScore = step3EngAvgScore;
    }

    public static MockExamScoresResponse from(FinalGrade finalGrade) {
        return new MockExamScoresResponse(
                finalGrade.getStep1Score(),
                finalGrade.getStep2KorScore(),
                finalGrade.getStep2EngScore(),
                finalGrade.getStep3KorScore(),
                finalGrade.getStep3EngScore()
        );
    }
}
