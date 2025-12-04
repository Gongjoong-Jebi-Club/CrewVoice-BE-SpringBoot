package org.skala.crewvoice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 음성 파일 언어 타입
 */
@Getter
@RequiredArgsConstructor
public enum Language {

    KOREAN("ko-KR", "한국어"),
    ENGLISH("en-US", "영어");

    @JsonValue
    private final String code;
    private final String displayName;

    /**
     * JSON 역직렬화를 위한 팩토리 메서드
     */
    @JsonCreator
    public static Language fromCode(String code) {
        for (Language language : Language.values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Invalid language code: " + code + ". Must be 'kor' or 'eng'");
    }

    /**
     * 언어 코드에 따라 한국어/영어 URL 선택
     */
    public String selectUrl(String koreanUrl, String englishUrl) {
        return this == KOREAN ? koreanUrl : englishUrl;
    }
}
