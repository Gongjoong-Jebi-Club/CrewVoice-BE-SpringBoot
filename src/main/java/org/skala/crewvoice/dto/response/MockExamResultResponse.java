package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.FinalGrade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MockExamResultResponse {
    private Long evaluationId;
    private LocalDateTime date;
    private BigDecimal totalScore;

    public static MockExamResultResponse from(FinalGrade finalGrade) {
        return new MockExamResultResponse(
                finalGrade.getEvaluation().getId(),
                finalGrade.getCreatedAt(),
                finalGrade.getTotalScore()
        );
    }
}
