package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.service.QuestionBankService;
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
}