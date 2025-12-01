package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.dto.request.QuestionRequest;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionResponse;
import org.skala.crewvoice.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Question Controller", description = "문제 관련 API")
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
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

    @Operation(summary = "STEP2 문제 편집 (추가/수정)", description = "STEP2 문제를 새로 등록하거나 기존 문제를 수정합니다. questionId가 있으면 수정, 없으면 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문제 편집 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP2 문제 편집에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step2")
    public ResponseEntity<ApiResponse<QuestionEditResponse>> upsertStep2Question(@RequestBody QuestionRequest request) {
        QuestionEditResponse response = questionService.upsertStep2Question(request);
        ApiResponse<QuestionEditResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }
}
