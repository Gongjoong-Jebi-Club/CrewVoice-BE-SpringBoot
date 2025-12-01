package org.skala.crewvoice.controller;

import org.skala.crewvoice.dto.request.ResetPasswordRequest;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.DeleteUserResponse;
import org.skala.crewvoice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Controller", description = "사용자 관리 관련 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "사용자 비밀번호 재설정", description = "서비스 운영자가 일반 사용자의 비밀번호를 새 비밀번호로 재설정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "특정 사용자 삭제", description = "서비스 운영자가 특정 사용자의 정보를 삭제합니다 (Soft Delete).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> deleteUser(@PathVariable Long userId) {
        DeleteUserResponse deleteUserResponse = userService.deleteUser(userId);
        ApiResponse<DeleteUserResponse> apiResponse = ApiResponse.success(deleteUserResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
