package org.skala.crewvoice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String name;
    @JsonProperty("employee_id")
    private String employeeId;
    private String email;
    private String password;
    private String role;
    private String gender;
}
