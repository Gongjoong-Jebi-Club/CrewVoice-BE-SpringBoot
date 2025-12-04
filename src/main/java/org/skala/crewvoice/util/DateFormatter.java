package org.skala.crewvoice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 포맷팅 유틸리티 클래스
 */
public class DateFormatter {

    private static final DateTimeFormatter MM_DD_FORMATTER = DateTimeFormatter.ofPattern("MM/dd");

    /**
     * ISO 8601 형식의 날짜 문자열을 MM/dd 형식으로 변환
     *
     * @param dateString ISO 8601 형식 날짜 문자열 (예: "2025-12-25 09:00:00+09", "2025-12-25T09:00:00")
     * @return MM/dd 형식 문자열 (예: "12/25")
     */
    public static String formatToMMdd(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            // "2025-12-25 09:00:00+09" 또는 "2025-12-25T09:00:00" 형식 처리
            // 공백이 있으면 'T'로 변경하고, 타임존 정보 제거
            String normalizedDate = dateString
                    .replace(" ", "T")
                    .replaceAll("[+-]\\d{2}(:\\d{2})?$", ""); // 타임존 제거

            // 시간 정보가 있으면 제거 (날짜만 남김)
            if (normalizedDate.contains("T")) {
                normalizedDate = normalizedDate.split("T")[0];
            }

            // LocalDate로 파싱 후 MM/dd 포맷으로 변환
            LocalDateTime dateTime = LocalDateTime.parse(normalizedDate + "T00:00:00");
            return dateTime.format(MM_DD_FORMATTER);
        } catch (Exception e) {
            // 파싱 실패 시 원본 반환
            return dateString;
        }
    }
}
