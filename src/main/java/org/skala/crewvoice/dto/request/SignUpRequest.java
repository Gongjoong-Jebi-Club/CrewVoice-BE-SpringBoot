package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.skala.crewvoice.annotation.Sensitive;
import org.skala.crewvoice.config.SensitiveDataSerializer;

@Getter
@Setter
public class SignUpRequest {
    private String name;

    @JsonProperty("employee_id")
    private String employeeId;

    private String email;

    @Sensitive
    @JsonSerialize(using = SensitiveDataSerializer.class)
    private String password;

    private String role;
    private String gender;

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "name='" + name + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", email='" + email + '\'' +
                ", password='***MASKED***'" +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
