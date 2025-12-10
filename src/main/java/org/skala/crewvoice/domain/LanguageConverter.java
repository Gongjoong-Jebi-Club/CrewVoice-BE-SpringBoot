package org.skala.crewvoice.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Language Enum을 DB에 code 값("ko-KR", "en-US")으로 저장하기 위한 Converter
 *
 * <p>변환 규칙:</p>
 * <ul>
 *   <li>Java Enum: Language.KOREAN → DB: "ko-KR"</li>
 *   <li>Java Enum: Language.ENGLISH → DB: "en-US"</li>
 * </ul>
 */
@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

    /**
     * Entity → Database
     * Language Enum을 code 값으로 변환하여 DB에 저장
     *
     * @param language Language Enum (KOREAN 또는 ENGLISH)
     * @return code 값 ("ko-KR" 또는 "en-US")
     */
    @Override
    public String convertToDatabaseColumn(Language language) {
        if (language == null) {
            return null;
        }
        return language.getCode();  // "ko-KR" 또는 "en-US"
    }

    /**
     * Database → Entity
     * DB에 저장된 code 값을 Language Enum으로 변환
     *
     * @param code DB에 저장된 값 ("ko-KR" 또는 "en-US")
     * @return Language Enum (KOREAN 또는 ENGLISH)
     */
    @Override
    public Language convertToEntityAttribute(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return Language.fromCode(code);
    }
}
