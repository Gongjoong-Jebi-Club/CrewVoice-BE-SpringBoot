package org.skala.crewvoice.domain;

/**
 * 평가 유형
 *
 * <p>평가(시험)의 종류를 구분합니다.
 */
public enum EvaluationType {

    /**
     * 실전 평가 (실제 시험)
     */
    EXAM("exam"),

    /**
     * 모의 평가 (연습용 시험)
     */
    MOCK("mock");

    private final String value;

    EvaluationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
