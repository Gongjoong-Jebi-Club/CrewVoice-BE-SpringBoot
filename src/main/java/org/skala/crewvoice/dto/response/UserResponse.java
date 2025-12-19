package org.skala.crewvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.domain.User;

import java.time.OffsetDateTime;

@Schema(description = "사용자 정보 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @Schema(description = "사용자 ID (PK)", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "김승무")
    private String name;

    @Schema(description = "사번", example = "2023001")
    private String employeeId;

    @Schema(description = "이메일", example = "crew@airkorea.com")
    private String email;

    @Schema(description = "사용자 역할 (ADMIN, MANAGER, CREW)", example = "CREW")
    private String role;

    @Schema(description = "가입일시 (yyyy.MM.dd 포맷)", example = "2023.10.25", type = "string")
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
