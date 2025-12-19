package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.annotation.Sensitive;
import org.skala.crewvoice.config.SensitiveDataSerializer;

@Schema(description = "비밀번호 재설정 요청 DTO (관리자용)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @Schema(description = "비밀번호를 재설정할 대상 사용자 ID (PK)", example = "1")
    private Long userId;

    @Schema(description = "새로운 비밀번호", example = "newPassword123!")
    @Sensitive
    @JsonSerialize(using = SensitiveDataSerializer.class)
    private String password;

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "userId=" + userId +
                ", password='***MASKED***'" +
                '}';
    }
}
