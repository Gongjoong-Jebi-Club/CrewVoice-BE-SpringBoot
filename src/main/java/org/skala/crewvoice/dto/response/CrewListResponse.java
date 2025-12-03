package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 평가 대상자 목록 응답 DTO (Wrapper)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewListResponse {

    @JsonProperty("crew_info")
    private List<CrewInfoResponse> crewInfo;

    public static CrewListResponse of(List<CrewInfoResponse> crewInfo) {
        return CrewListResponse.builder()
                .crewInfo(crewInfo)
                .build();
    }
}