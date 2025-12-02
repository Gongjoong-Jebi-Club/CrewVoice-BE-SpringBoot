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
import org.skala.crewvoice.dto.response.QuestionStep2DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep2RandomResponse;
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
    @GetMapping("/step2/list/{pageNumber}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getStep2Questions(@PathVariable int pageNumber) {
        List<QuestionResponse> questions = questionStep2Service.getStep2Questions(pageNumber);
        ApiResponse<List<QuestionResponse>> response = ApiResponse.success(questions);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "STEP2 특정 문제 정보 조회", description = "STEP2 문제 편집 페이지에서 수정 창에 기본값으로 들어갈 문제 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/step2/{questionId}")
    public ResponseEntity<ApiResponse<QuestionStep2DetailResponse>> getStep2QuestionById(@PathVariable Long questionId) {
        QuestionStep2DetailResponse response = questionStep2Service.getQuestionById(questionId);
        ApiResponse<QuestionStep2DetailResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
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

    @Operation(summary = "STEP2 문제 조회 (출제)", description = "Step2 문제 시작 시 호출되는 API로, Step2 문제은행에서 랜덤으로 한 문제를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/step2")
    public ResponseEntity<ApiResponse<QuestionStep2RandomResponse>> getStep2RandomQuestion() {
        QuestionStep2RandomResponse response = questionStep2Service.getRandomQuestion();
        ApiResponse<QuestionStep2RandomResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }
}
