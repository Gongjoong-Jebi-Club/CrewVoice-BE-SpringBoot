package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.User;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String employeeId;
    private String email;
    private String role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private OffsetDateTime createdAt;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmployeeId(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
