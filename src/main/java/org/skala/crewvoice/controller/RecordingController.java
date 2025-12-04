package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.request.RecordingDownloadRequest;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.RecordingDownloadResponse;
import org.skala.crewvoice.service.RecordingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 녹음 파일 다운로드 API Controller (POST 방식)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recordings")
@RequiredArgsConstructor
@Tag(name = "Recording", description = "녹음 파일 관리 API")
public class RecordingController {

    private final RecordingService recordingService;

    /**
     * 녹음 파일 다운로드 URL 생성 (POST 방식)
     *
     * @param request 다운로드 요청 정보
     * @return Pre-signed URL 정보
     */
    @Operation(
            summary = "녹음 파일 다운로드 URL 생성",
            description = "특정 평가의 특정 사용자 녹음 파일에 대한 다운로드 URL을 생성합니다. (15분 유효)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "URL 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 파라미터"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "제출 내역 또는 파일을 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    @PostMapping("/download")
    public ResponseEntity<ApiResponse<RecordingDownloadResponse>> getRecordingDownloadUrl(
            @Valid @RequestBody RecordingDownloadRequest request
    ) {
        log.info("POST /api/v1/recordings/download - evaluationId: {}, userId: {}, step: {}, question: {}, language: {}",
                request.getEvaluationId(), request.getUserId(), request.getStep(),
                request.getQuestion(), request.getLanguage());

        RecordingDownloadResponse response = recordingService.getRecordingDownloadUrl(request);

        log.info("녹음 파일 다운로드 URL 생성 성공 - userId: {}, fileName: {}",
                request.getUserId(), response.getFileName());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}