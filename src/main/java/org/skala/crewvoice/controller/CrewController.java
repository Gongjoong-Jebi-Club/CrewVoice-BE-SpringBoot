package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.CrewListResponse;
import org.skala.crewvoice.service.CrewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 평가 대상자(Crew) 관련 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
@Tag(name = "Crew", description = "평가 대상자 관리 API")
public class CrewController {

    private final CrewService crewService;

    /**
     * 특정 평가의 대상자 정보 조회
     *
     * @param evaluationId 평가 ID
     * @return 평가 대상자 목록
     */
    @Operation(summary = "평가 대상자 정보 조회", description = "특정 평가의 모든 대상자 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<CrewListResponse>> getCrewInfoByEvaluation(
            @Parameter(description = "평가 ID", required = true)
            @PathVariable Long evaluationId
    ) {
        log.info("GET /api/v1/crews/{} - 평가 대상자 정보 조회 요청", evaluationId);

        CrewListResponse response = crewService.getCrewInfoByEvaluation(evaluationId);

        log.info("평가 대상자 정보 조회 성공 - 평가ID: {}, 대상자 수: {}",
                evaluationId, response.getCrewInfo().size());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}