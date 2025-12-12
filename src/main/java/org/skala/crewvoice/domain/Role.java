package org.skala.crewvoice.domain;

/**
 * 사용자 역할 Enum
 */
public enum Role {
    ADMIN,   // 관리자
    MANAGER, // 매니저 (평가 관리 권한)
    CREW     // 피평가자 (승무원)
}
