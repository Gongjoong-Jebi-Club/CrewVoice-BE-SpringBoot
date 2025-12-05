package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.CrewListResponse;
import org.skala.crewvoice.dto.response.CrewManagementResponse;
import org.skala.crewvoice.service.CrewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 전체 피평가자 정보 조회 및 검색 (관리자 전용)
     *
     * <p>관리자가 "피평가자 관리" 페이지에서 모든 CREW의 정보를 조회하거나 검색합니다.
     * 각 피평가자의 최신 EXAM 타입 한국어/영어 등급을 함께 반환합니다.
     *
     * @param keyword 검색 키워드 (이름 또는 사번, optional)
     * @return 전체 또는 검색된 피평가자 목록 (최신 EXAM 등급 포함)
     */
    @Operation(summary = "전체 피평가자 정보 조회 및 검색",
               description = "모든 CREW의 정보와 최신 EXAM 등급을 조회합니다. 키워드로 이름 또는 사번을 검색할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피평가자 정보를 성공적으로 조회했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다. (ADMIN 또는 MANAGER 필요)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<CrewManagementResponse>>> getAllCrews(
            @Parameter(description = "검색 키워드 (이름 또는 사번)", required = false)
            @RequestParam(required = false) String keyword
    ) {
        log.info("GET /api/v1/crews - 피평가자 정보 조회 요청 (keyword: {})", keyword);

        List<CrewManagementResponse> responses = crewService.getAllCrewsWithLatestGrades(keyword);

        log.info("피평가자 정보 조회 성공 - 총 {}명", responses.size());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

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