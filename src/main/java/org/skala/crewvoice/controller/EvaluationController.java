package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.EvaluationCreateResponse;
import org.skala.crewvoice.dto.response.EvaluationListResponse;
import org.skala.crewvoice.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 평가(시험) 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluation Controller", description = "평가(시험) 관리 관련 API")
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * 전체 평가 목록 조회
     */
    @Operation(
            summary = "평가 목록 조회",
            description = "평가관리 페이지에서 생성된 모든 평가(시험)를 조회합니다. " +
                    "각 평가의 대상 인원 수와 완료 인원 수가 포함됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "평가 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<EvaluationListResponse>> getAllEvaluations() {
        log.info("평가 목록 조회 요청 수신");

        // 서비스 호출
        EvaluationListResponse response = evaluationService.getAllEvaluations();

        // 응답 반환
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 평가(시험) 생성
     */
    @Operation(
            summary = "평가 생성",
            description = "평가 생성 마법사 페이지에서 평가(시험)를 생성합니다. " +
                    "6가지 평가 항목의 가중치 합계는 100이어야 합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "평가 생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (가중치 합계 오류, 날짜 범위 오류 등)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationCreateResponse>> createEvaluation(
            @Valid @RequestBody EvaluationCreateRequest request
    ) {
        log.info("평가 생성 요청 수신 - 평가명: {}", request.getName());

        // 서비스 호출
        EvaluationCreateResponse response = evaluationService.createEvaluation(request);

        // 응답 반환
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}