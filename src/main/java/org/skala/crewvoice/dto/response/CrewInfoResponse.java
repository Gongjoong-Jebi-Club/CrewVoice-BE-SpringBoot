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
    private String id;  // user_id를 String으로 변환

    @JsonProperty("name")
    private String name;

    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("state")
    private Boolean state;  // 시험(평가) 시행 여부

    @JsonProperty("completion_time")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private OffsetDateTime completionTime;  // 시험 시행 시간

    @JsonProperty("step1_score")
    private BigDecimal step1Score;

    @JsonProperty("step2_score")
    private BigDecimal step2Score;

    @JsonProperty("step3_score")
    private BigDecimal step3Score;

    @JsonProperty("total_score")
    private BigDecimal totalScore;

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
                .state(finalGrade != null)  // finalGrade가 있으면 true, 없으면 false
                .completionTime(finalGrade != null ? finalGrade.getCreatedAt() : null)
                .step1Score(finalGrade != null ? finalGrade.getStep1Score() : null)
                .step2Score(finalGrade != null ? finalGrade.getStep2Score() : null)
                .step3Score(finalGrade != null ? finalGrade.getStep3Score() : null)
                .totalScore(finalGrade != null ? finalGrade.getTotalScore() : null)
                .build();
    }
}