package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.response.MockExamResultResponse;
import org.skala.crewvoice.service.MockExamResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Mock Exam Result Controller", description = "모의고사 결과 관련 API")
@RestController
@RequestMapping("/api/v1/mock-exams")
public class MockExamResultController {

    private final MockExamResultService mockExamResultService;

    public MockExamResultController(MockExamResultService mockExamResultService) {
        this.mockExamResultService = mockExamResultService;
    }

    @Operation(summary = "내 모의고사 결과 조회", description = "현재 로그인한 사용자의 모의고사 일자 및 점수를 최신 응시순으로 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<List<MockExamResultResponse>>> getMyMockExamResults(
            @AuthenticationPrincipal User user
    ) {
        List<MockExamResultResponse> results = mockExamResultService.getMyMockExamResults(user);
        ApiResponse<List<MockExamResultResponse>> response = ApiResponse.success(results);
        return ResponseEntity.ok(response);
    }
}
