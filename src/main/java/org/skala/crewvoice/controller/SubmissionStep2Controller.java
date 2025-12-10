package org.skala.crewvoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.request.SubmissionStep2Request;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.dto.response.SubmissionStep2Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.skala.crewvoice.service.SubmissionStep2Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * STEP2 제출 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/submissions/step2")
@RequiredArgsConstructor
@Tag(name = "Submission Controller", description = "답안 제출 관련 API")
public class SubmissionStep2Controller {

    private final SubmissionStep2Service submissionStep2Service;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * STEP2 음성 파일 제출
     *
     * @param user 인증된 사용자
     * @param audioFile 음성 파일 (.wav)
     * @param metadata JSON 형식의 메타데이터
     * @return 제출 응답 정보
     */
    @Operation(summary = "STEP2 음성 파일 제출",
               description = "STEP2 문제에 대한 사용자의 음성 녹음 파일을 제출합니다. " +
                             "음성 파일을 S3에 저장하고 제출 정보를 submissions_step2 테이블에 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "제출 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 또는 문제를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubmissionStep2Response>> submitAudio(
            @AuthenticationPrincipal User user,
            @Parameter(description = "음성 파일 (.wav 형식)", required = true,
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("audioFile") MultipartFile audioFile,
            @Parameter(description = "제출 메타데이터 (JSON)", required = true,
                      schema = @Schema(implementation = SubmissionStep2Request.class))
            @RequestPart("metadata") String metadata
    ) {
        try {
            log.info("POST /api/v1/submissions/step2 - 음성 파일 제출 요청 (userId: {})", user.getId());

            // JSON metadata를 DTO로 변환
            SubmissionStep2Request request = objectMapper.readValue(metadata, SubmissionStep2Request.class);

            // 수동 Validation
            Set<ConstraintViolation<SubmissionStep2Request>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                log.error("Validation 실패 - {}", errorMessage);
                throw new InvalidRequestException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // 서비스 호출
            SubmissionStep2Response response = submissionStep2Service.submitAudio(user, audioFile, request);

            log.info("음성 파일 제출 성공 - submissionId: {}", response.getSubmissionId());

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("음성 파일 제출 실패 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("음성 파일 제출 처리 중 오류가 발생했습니다.", e);
        }
    }
}
