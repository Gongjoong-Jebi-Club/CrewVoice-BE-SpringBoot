package org.skala.crewvoice.controller;

import org.skala.crewvoice.dto.request.ResetPasswordRequest;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.DeleteUserResponse;
import org.skala.crewvoice.dto.response.UserResponse;
import org.skala.crewvoice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "User Controller", description = "사용자 관리 관련 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "전체 사용자 조회 및 검색", description = "서비스 운영자가 사용자 관리 페이지에서 전체 사용자를 조회하거나, 키워드(이름, 사번, 이메일)와 역할로 ACTIVE 상태의 사용자를 검색합니다. 검색 조건이 없으면 전체 활성 사용자를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        List<UserResponse> users = userService.searchUsers(keyword, role);
        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.success(users);
        return ResponseEntity.ok(apiResponse);
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
