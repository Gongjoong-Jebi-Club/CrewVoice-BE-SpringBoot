package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.User;

import java.time.format.DateTimeFormatter;

/**
 * 피평가자 관리 응답 DTO
 * 
 * <p>관리자가 "피평가자 관리" 페이지에서 조회하는 전체 CREW 정보입니다.
 * 각 피평가자의 최신 한국어/영어 등급을 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewManagementResponse {

    /**
     * 사용자 ID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 이름
     */
    @JsonProperty("name")
    private String name;

    /**
     * 사번
     */
    @JsonProperty("employeeId")
    private String employeeId;

    /**
     * 한국어 등급 (A, B, 등급없음)
     */
    @JsonProperty("korGrade")
    private String korGrade;

    /**
     * 영어 등급 (A, B, 등급없음)
     */
    @JsonProperty("engGrade")
    private String engGrade;

    /**
     * 최종 성적 생성일 (YYYY-MM-DD 형식)
     * 등급이 없으면 null
     */
    @JsonProperty("createdAt")
    private String createdAt;

    /**
     * User와 FinalGrade로부터 CrewManagementResponse 생성
     *
     * @param user 사용자 정보
     * @param finalGrade 최종 성적 정보 (nullable)
     * @return CrewManagementResponse
     */
    public static CrewManagementResponse from(User user, FinalGrade finalGrade) {
        String formattedDate = null;
        if (finalGrade != null && finalGrade.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formattedDate = finalGrade.getCreatedAt().format(formatter);
        }

        return CrewManagementResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .korGrade(finalGrade != null ? finalGrade.getKorGrade() : "등급없음")
                .engGrade(finalGrade != null ? finalGrade.getEngGrade() : "등급없음")
                .createdAt(formattedDate)
                .build();
    }
}
