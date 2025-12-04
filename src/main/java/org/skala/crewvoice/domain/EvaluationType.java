package org.skala.crewvoice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 평가 유형
 *
 * <p>평가(시험)의 종류를 구분합니다.
 */
public enum EvaluationType {

    /**
     * 실전 평가 (실제 시험)
     */
    EXAM("EXAM"),

    /**
     * 모의 평가 (연습용 시험)
     */
    MOCK("MOCK");

    private final String value;

    EvaluationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * JSON 역직렬화를 위한 팩토리 메서드
     * "EXAM" 또는 "MOCK" 문자열을 Enum으로 변환
     */
    @JsonCreator
    public static EvaluationType fromValue(String value) {
        if (value == null) {
            return null;
        }

        for (EvaluationType type : EvaluationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
            "Invalid evaluation_type: " + value + ". Must be 'EXAM' or 'MOCK'"
        );
    }
}
