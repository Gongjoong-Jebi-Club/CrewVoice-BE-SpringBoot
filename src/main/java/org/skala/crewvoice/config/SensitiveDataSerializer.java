package org.skala.crewvoice.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 민감한 데이터를 마스킹 처리하는 Jackson 시리얼라이저
 * 로그나 API 응답에서 비밀번호 등이 노출되지 않도록 보호
 */
public class SensitiveDataSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeString("***MASKED***");
    }
}
