package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.service.QuestionBankService;
import org.skala.crewvoice.dto.response.QuestionBankSummaryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 문제 은행 관리 컨트롤러
 * - Manager 권한으로 STEP2, STEP3 문제 관리
 */
@Slf4j
@Tag(name = "Question Bank", description = "문제 은행 관리 API")
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    /**
     * STEP2 또는 STEP3 문제 전체 리스트 조회
     *
     * @param step "step2" 또는 "step3"
     * @return 문제 리스트
     */
    @Operation(summary = "STEP2/3 문제 리스트 조회",
            description = "해당 STEP의 모든 문제를 조회합니다. (Pagination 없음)")
    @GetMapping("/{step}")
    public ResponseEntity<ApiResponse<List<?>>> getQuestions(
            @Parameter(description = "조회할 STEP (step2 또는 step3)", required = true, example = "step2")
            @PathVariable String step) {

        log.info("GET /api/v1/questions/{} - 문제 리스트 조회 요청", step);

        List<?> questions = questionBankService.getQuestionsByStep(step);

        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    /**
     * API 13: 문제은행 기본 정보 조회
     *
     * 관리자 권한으로 문제은행 페이지에 접속하여
     * STEP별 문제 개수와 최종 수정 일자를 조회
     *
     * @return STEP별 문제 통계 정보 (step1, step2, step3)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "문제은행 기본 정보 조회",
            description = "관리자 권한으로 STEP별 문제 개수와 최종 수정 일자를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = QuestionBankSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 로그인이 필요합니다"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요합니다"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 문제은행 정보를 불러오지 못했습니다"
            )
    })
    public ResponseEntity<ApiResponse<QuestionBankSummaryResponse>> getQuestionBankSummary() {
        log.info("[API 13] 문제은행 기본 정보 조회 요청");

        QuestionBankSummaryResponse response = questionBankService.getQuestionBankSummary();

        log.info("[API 13] 문제은행 기본 정보 조회 성공");

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}