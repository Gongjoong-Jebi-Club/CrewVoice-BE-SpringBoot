package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.skala.crewvoice.dto.request.LoginRequest;
import org.skala.crewvoice.dto.request.SignUpRequest;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.LoginResponse;
import org.skala.crewvoice.dto.response.SignUpResponse;
import org.skala.crewvoice.dto.response.TokenRefreshResponse;
import org.skala.crewvoice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Controller", description = "회원 가입 및 로그인/로그아웃 관련 API")
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = authService.signUp(signUpRequest);
        ApiResponse<SignUpResponse> apiResponse = ApiResponse.success(signUpResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.success(loginResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "로그아웃", description = "사용자의 액세스 토큰을 만료시켜 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "토큰 재발급", description = "Authorization 헤더의 만료된 Access Token에서 사용자 정보를 파악하여 새로운 Access Token을 발급받습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (Refresh Token 유효성 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshAccessToken(HttpServletRequest request) {
        TokenRefreshResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "[테스트용] 만료된 토큰 생성", description = "테스트를 위해 이미 만료된 Access Token을 생성합니다. 이 토큰으로 /refresh API를 테스트할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "만료된 토큰 생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/test/expired-token")
    public ResponseEntity<ApiResponse<LoginResponse>> createExpiredToken(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.createExpiredTokenForTest(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
