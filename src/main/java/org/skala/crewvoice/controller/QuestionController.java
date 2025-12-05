package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skala.crewvoice.common.ApiResponse;
import org.skala.crewvoice.dto.request.QuestionRequest;
import org.skala.crewvoice.dto.request.QuestionStep1Request;
import org.skala.crewvoice.dto.request.QuestionStep2Request;
import org.skala.crewvoice.dto.request.QuestionStep3RandomRequest;
import org.skala.crewvoice.dto.request.QuestionStep3Request;
import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.dto.response.QuestionResponse;
import org.skala.crewvoice.dto.response.QuestionStep1Response;
import org.skala.crewvoice.dto.response.QuestionStep2DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep2RandomResponse;
import org.skala.crewvoice.dto.response.QuestionStep3DetailResponse;
import org.skala.crewvoice.dto.response.QuestionStep3ListResponse;
import org.skala.crewvoice.dto.response.QuestionStep3RandomResponse;
import org.skala.crewvoice.service.QuestionService;
import org.skala.crewvoice.service.QuestionStep1Service;
import org.skala.crewvoice.service.QuestionStep2Service;
import org.skala.crewvoice.service.QuestionStep3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Question Controller", description = "문제 관련 API")
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionStep1Service questionStep1Service;
    private final QuestionStep2Service questionStep2Service;
    private final QuestionStep3Service questionStep3Service;

    public QuestionController(QuestionService questionService, QuestionStep1Service questionStep1Service,
                              QuestionStep2Service questionStep2Service, QuestionStep3Service questionStep3Service) {
        this.questionService = questionService;
        this.questionStep1Service = questionStep1Service;
        this.questionStep2Service = questionStep2Service;
        this.questionStep3Service = questionStep3Service;
    }

    @Operation(summary = "STEP1 문제 편집", description = "문제은행의 STEP1 문제 추가/수정 화면에서 문제를 편집합니다. questionId가 null이면 추가, 값이 있으면 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP1 문제 편집에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step1/{questionId}")
    public ResponseEntity<ApiResponse<QuestionStep1Response>> editStep1Question(@RequestBody QuestionStep1Request request) {
        QuestionStep1Response response = questionStep1Service.saveOrUpdateQuestion(request);
        ApiResponse<QuestionStep1Response> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
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

    @Operation(summary = "STEP2 문제 편집", description = "문제은행의 STEP2 문제 추가/수정 화면에서 문제를 편집합니다. questionId가 null이면 추가, 값이 있으면 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문제 편집 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP2 문제 편집에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step2/edit")
    public ResponseEntity<ApiResponse<QuestionEditResponse>> editStep2Question(@RequestBody QuestionStep2Request request) {
        QuestionEditResponse response = questionStep2Service.saveOrUpdateQuestion(request);
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

    @Operation(summary = "STEP3 문제 목록 조회", description = "STEP3의 문제를 리스트 형식으로 조회합니다 (페이지당 10개).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "STEP3 문제 목록 조회에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/step3/list/{pageNumber}")
    public ResponseEntity<ApiResponse<List<QuestionStep3ListResponse>>> getStep3Questions(@PathVariable int pageNumber) {
        List<QuestionStep3ListResponse> questions = questionStep3Service.getStep3Questions(pageNumber);
        ApiResponse<List<QuestionStep3ListResponse>> response = ApiResponse.success(questions);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "STEP3 특정 문제 정보 조회", description = "STEP3 문제 편집 페이지에서 수정 창에 기본값으로 들어갈 문제 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/step3/{questionId}")
    public ResponseEntity<ApiResponse<QuestionStep3DetailResponse>> getStep3QuestionById(@PathVariable Long questionId) {
        QuestionStep3DetailResponse response = questionStep3Service.getQuestionById(questionId);
        ApiResponse<QuestionStep3DetailResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "STEP3 문제 편집", description = "문제은행의 STEP3 문제 추가/수정 화면에서 문제를 편집합니다. questionId가 null이면 추가, 값이 있으면 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "문제를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step3/edit")
    public ResponseEntity<ApiResponse<QuestionEditResponse>> editStep3Question(@RequestBody QuestionStep3Request request) {
        QuestionEditResponse response = questionStep3Service.saveOrUpdateQuestion(request);
        ApiResponse<QuestionEditResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "STEP3 문제 조회 (출제)", description = "모의고사 Step3 문제 넘어갈 때마다 호출되는 API로, Step3의 문제를 하나 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "사용자의 로그인 검증을 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/step3/random")
    public ResponseEntity<ApiResponse<QuestionStep3RandomResponse>> getStep3RandomQuestion(@RequestBody QuestionStep3RandomRequest request) {
        QuestionStep3RandomResponse response = questionStep3Service.getRandomQuestion(request);
        ApiResponse<QuestionStep3RandomResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }
}
