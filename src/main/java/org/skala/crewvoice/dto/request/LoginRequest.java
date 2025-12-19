package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.skala.crewvoice.annotation.Sensitive;
import org.skala.crewvoice.config.SensitiveDataSerializer;

@Schema(description = "로그인 요청 DTO")
@Getter
@Setter
public class LoginRequest {
    @Schema(description = "사용자 이메일", example = "crew@airkorea.com")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123!")
    @Sensitive
    @JsonSerialize(using = SensitiveDataSerializer.class)
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='***MASKED***'" +
                '}';
    }
}
