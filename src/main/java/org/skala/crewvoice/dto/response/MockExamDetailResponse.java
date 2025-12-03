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
        return new MockExamDetailResponse(
                finalGrade.getTotalScore(),
                finalGrade.getStep1Score(),
                finalGrade.getStep2Score(),
                finalGrade.getStep3Score()
        );
    }
}
