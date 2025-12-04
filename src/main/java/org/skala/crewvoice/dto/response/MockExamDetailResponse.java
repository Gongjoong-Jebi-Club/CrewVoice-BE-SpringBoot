package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.FinalGrade;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MockExamDetailResponse {
    private BigDecimal totalScore;
    private BigDecimal step1Score;
    private BigDecimal step2Score;
    private BigDecimal step3Score;

    public static MockExamDetailResponse from(FinalGrade finalGrade) {
        // Calculate scores from individual components
        BigDecimal totalScore = finalGrade.getStep1Score()
                .add(finalGrade.getStep2KorScore())
                .add(finalGrade.getStep2EngScore())
                .add(finalGrade.getStep3KorScore())
                .add(finalGrade.getStep3EngScore());

        BigDecimal step2Score = finalGrade.getStep2KorScore()
                .add(finalGrade.getStep2EngScore());

        BigDecimal step3Score = finalGrade.getStep3KorScore()
                .add(finalGrade.getStep3EngScore());

        return new MockExamDetailResponse(
                totalScore,
                finalGrade.getStep1Score(),
                step2Score,
                step3Score
        );
    }
}
