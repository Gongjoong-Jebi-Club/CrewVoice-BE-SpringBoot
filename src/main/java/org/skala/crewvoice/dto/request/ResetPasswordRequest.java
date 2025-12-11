package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.annotation.Sensitive;
import org.skala.crewvoice.config.SensitiveDataSerializer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private Long userId;

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
