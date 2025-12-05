package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.FinalGrade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MockExamResultResponse {
    private Long evaluationId;
    private OffsetDateTime date;
    private BigDecimal totalScore;

    public static MockExamResultResponse from(FinalGrade finalGrade) {
        // Calculate total score from step scores
        BigDecimal totalScore = finalGrade.getStep1Score()
                .add(finalGrade.getStep2KorScore())
                .add(finalGrade.getStep2EngScore())
                .add(finalGrade.getStep3KorScore())
                .add(finalGrade.getStep3EngScore());

        return new MockExamResultResponse(
                finalGrade.getEvaluation().getEvaluationId(),
                finalGrade.getCreatedAt(),
                totalScore
        );
    }
}
