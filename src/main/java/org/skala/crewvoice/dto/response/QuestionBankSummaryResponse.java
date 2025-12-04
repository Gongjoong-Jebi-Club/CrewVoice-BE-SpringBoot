package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 문제은행 기본 정보 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBankSummaryResponse {

    /**
     * 문제은행 통계 리스트 (STEP1, STEP2, STEP3)
     */
    private List<StepSummary> data;

    /**
     * 각 STEP별 통계 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepSummary {

        /**
         * STEP 구분 (step1, step2, step3)
         */
        private String step;

        /**
         * 총 문제 수
         */
        private Integer questionCount;

        /**
         * 최종 수정 일시 (YYYY.MM.DD 형식)
         */
        private String updatedAt;

        /**
         * OffsetDateTime을 YYYY.MM.DD 형식으로 변환하여 생성
         */
        public static StepSummary of(String step, Integer questionCount, OffsetDateTime updatedAt) {
            String formattedDate = null;

            if (updatedAt != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                formattedDate = updatedAt.format(formatter);
            }

            return StepSummary.builder()
                    .step(step)
                    .questionCount(questionCount)
                    .updatedAt(formattedDate)
                    .build();
        }
    }
}