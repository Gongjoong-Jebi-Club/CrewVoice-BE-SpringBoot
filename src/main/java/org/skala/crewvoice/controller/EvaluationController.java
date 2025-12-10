package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.request.CallbackRequest;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;
import org.skala.crewvoice.dto.request.EvaluationStartRequest;
import org.skala.crewvoice.dto.response.*;
import org.skala.crewvoice.repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 전체 평가 목록 조회 (관리자용)
     */
    @Operation(
            summary = "전체 평가 목록 조회 (관리자용)",
            description = "관리자가 평가관리 페이지에서 생성된 모든 평가(시험)를 조회합니다. " +
                    "평가 기간과 상관없이 모든 평가를 조회하며, " +
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
        log.info("GET /api/v1/evaluations - 전체 평가 목록 조회 요청 (관리자용)");

        // 서비스 호출
        EvaluationListResponse response = evaluationService.getAllEvaluations();

        log.info("전체 평가 목록 조회 성공 - 평가 수: {}", response.getEvaluations().size());

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

    /**
     * 현재 진행 중인 평가 목록 조회 (피평가자용)
     *
     * @return 진행 중인 평가 목록
     */
    @Operation(
            summary = "현재 진행 중인 평가 조회 (피평가자용)",
            description = "피평가자가 현재 진행 중인 평가 목록을 조회합니다. " +
                    "현재 시간이 평가 기간(startDate ~ endDate) 내에 있는 평가만 반환됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ActiveEvaluationListResponse>> getActiveEvaluations() {
        log.info("GET /api/v1/evaluations/active - 현재 진행 중인 평가 조회 요청");

        ActiveEvaluationListResponse response = evaluationService.getActiveEvaluations();

        log.info("현재 진행 중인 평가 조회 성공 - 평가 수: {}", response.getEvaluations().size());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 평가 시작 (3번 API)
     * 제출된 음성 파일에 대한 평가를 FastAPI로 요청합니다.
     */
    @Operation(
            summary = "평가 시작 (3번 API)",
            description = "사용자가 제출한 음성 파일에 대한 평가를 시작합니다. " +
                    "FastAPI로 비동기 평가 요청을 전송하고 즉시 반환합니다 (Fire-and-Forget). " +
                    "평가는 백그라운드에서 진행되며, 완료되면 FastAPI가 5번 API(callback)를 호출합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "평가 시작 성공 (백그라운드 처리 중)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (제출 ID 없음)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "제출 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<EvaluationStartResponse>> startEvaluation(
            @Valid @RequestBody EvaluationStartRequest request
    ) {
        log.info("POST /api/v1/evaluations/start - 평가 시작 요청 - submissionId: {}, step: {}",
                request.getSubmissionId(), request.getStep());

        // 비동기로 평가 시작 (즉시 반환)
        evaluationService.startEvaluationAsync(request.getSubmissionId(), request.getStep());

        // 응답 생성
        EvaluationStartResponse response = EvaluationStartResponse.success(request.getSubmissionId());

        log.info("평가 시작 요청 처리 완료 - submissionId: {}, step: {} (백그라운드 처리 중)",
                request.getSubmissionId(), request.getStep());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * FastAPI 평가 완료 콜백 (5번 API)
     * FastAPI에서 평가가 완료되면 호출되는 콜백 API
     */
    @Operation(
            summary = "FastAPI 평가 완료 콜백 (5번 API)",
            description = "FastAPI에서 음성 평가가 완료되면 호출하는 콜백 API입니다. " +
                    "평가 결과(점수, 피드백)를 받아 데이터베이스에 저장합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "콜백 처리 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 데이터 누락)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "평가 결과를 찾을 수 없음 (유효하지 않은 task_id)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> handleCallback(
            @Valid @RequestBody CallbackRequest request
    ) {
        log.info("POST /api/v1/evaluations/callback - FastAPI 콜백 수신 - taskId: {}, status: {}",
                request.getTaskId(), request.getStatus());

        // 콜백 처리
        evaluationService.handleCallback(request);

        log.info("FastAPI 콜백 처리 완료 - taskId: {}, status: {}",
                request.getTaskId(), request.getStatus());

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}