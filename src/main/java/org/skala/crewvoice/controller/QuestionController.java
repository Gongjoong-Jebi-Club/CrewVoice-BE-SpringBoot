package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.dto.request.QuestionRequest;
import org.skala.crewvoice.dto.request.QuestionStep2Request;
import org.skala.crewvoice.dto.request.QuestionStep2UpdateRequest;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionResponse;
import org.skala.crewvoice.service.QuestionService;
import org.skala.crewvoice.service.QuestionStep2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Question Controller", description = "문제 관련 API")
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionStep2Service questionStep2Service;

    public QuestionController(QuestionService questionService, QuestionStep2Service questionStep2Service) {
        this.questionService = questionService;
        this.questionStep2Service = questionStep2Service;
    }

    @Operation(summary = "STEP2 문제 목록 조회", description = "STEP2의 문제를 리스트 형식으로 조회합니다 (페이지당 10개).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP2 조회에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/step2/{pageNumber}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getStep2Questions(@PathVariable int pageNumber) {
        List<QuestionResponse> questions = questionService.getStep2Questions(pageNumber);
        ApiResponse<List<QuestionResponse>> response = ApiResponse.success(questions);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "STEP2 문제 추가", description = "문제은행의 STEP2 문제 추가 화면에서 새로운 문제를 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문제 추가 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP2 문제 편집에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step2")
    public ResponseEntity<ApiResponse<QuestionEditResponse>> addStep2Question(@RequestBody QuestionStep2Request request) {
        QuestionEditResponse response = questionStep2Service.addQuestion(request);
        ApiResponse<QuestionEditResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "STEP2 문제 수정", description = "문제은행의 STEP2 문제 수정 화면에서 기존 문제를 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문제 수정 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP2 문제 편집에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/step2/{questionId}")
    public ResponseEntity<ApiResponse<QuestionEditResponse>> updateStep2Question(
            @PathVariable Long questionId,
            @RequestBody QuestionStep2UpdateRequest request) {
        QuestionEditResponse response = questionStep2Service.updateQuestion(questionId, request);
        ApiResponse<QuestionEditResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }
}
