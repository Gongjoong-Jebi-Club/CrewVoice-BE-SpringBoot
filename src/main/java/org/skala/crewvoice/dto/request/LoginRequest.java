package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.skala.crewvoice.annotation.Sensitive;
import org.skala.crewvoice.config.SensitiveDataSerializer;

@Getter
@Setter
public class LoginRequest {
    private String email;

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
