package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.request.SubmissionStep1Request;
import org.skala.crewvoice.dto.response.SubmissionStep1Response;
import org.skala.crewvoice.service.SubmissionStep1Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Submission Controller", description = "답안 제출 관련 API")
@RestController
@RequestMapping("/api/v1/submissions/step1")
@RequiredArgsConstructor
public class SubmissionStep1Controller {

    private final SubmissionStep1Service submissionStep1Service;

    @Operation(summary = "STEP1 답안 제출", description = "Step 1(객관식)에서 사용자가 옵션 중 택 1하고 다음 버튼 클릭 시 호출되는 API로, 사용자의 응답과 정답 여부를 확인(채점)해서 DB에 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionStep1Response>> submitAnswer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmissionStep1Request request) {
        SubmissionStep1Response response = submissionStep1Service.submitAnswer(user, request);
        ApiResponse<SubmissionStep1Response> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }
}
