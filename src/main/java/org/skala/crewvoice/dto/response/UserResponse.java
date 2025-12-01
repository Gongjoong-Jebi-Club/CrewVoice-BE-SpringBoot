package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.UserStatus;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;

    @JsonProperty("username")
    private String name;

    private String employeeId;
    private String email;
    private String role;
    private UserStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime createdAt;

    public static UserResponse from(org.skala.crewvoice.domain.User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmployeeId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
