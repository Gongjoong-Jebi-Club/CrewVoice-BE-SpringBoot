package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.User;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 평가 대상자 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewInfoResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("isCompleted")
    private Boolean isCompleted;

    @JsonProperty("completionTime")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private OffsetDateTime completionTime;

    @JsonProperty("step1Score")
    private BigDecimal step1Score;

    @JsonProperty("step2KorScore")
    private BigDecimal step2KorScore;

    @JsonProperty("step2EngScore")
    private BigDecimal step2EngScore;

    @JsonProperty("step3KorScore")
    private BigDecimal step3KorScore;

    @JsonProperty("step3EngScore")
    private BigDecimal step3EngScore;

    /**
     * User와 FinalGrade로부터 CrewInfoResponse 생성
     * @param user 사용자 정보
     * @param finalGrade 성적 정보 (nullable - 미완료 사용자의 경우 null)
     * @return CrewInfoResponse
     */
    public static CrewInfoResponse of(User user, FinalGrade finalGrade) {
        return CrewInfoResponse.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .isCompleted(finalGrade != null)
                .completionTime(finalGrade != null ? finalGrade.getCreatedAt() : null)
                .step1Score(finalGrade != null ? finalGrade.getStep1Score() : null)
                .step2KorScore(finalGrade != null ? finalGrade.getStep2KorScore() : null)
                .step2EngScore(finalGrade != null ? finalGrade.getStep2EngScore() : null)
                .step3KorScore(finalGrade != null ? finalGrade.getStep3KorScore() : null)
                .step3EngScore(finalGrade != null ? finalGrade.getStep3EngScore() : null)
                .build();
    }
}