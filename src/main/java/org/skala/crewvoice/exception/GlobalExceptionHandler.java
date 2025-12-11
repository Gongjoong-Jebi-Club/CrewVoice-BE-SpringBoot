package org.skala.crewvoice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.exception.custom.DuplicateEntityException;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidRequestException(InvalidRequestException e) {
        log.error("Invalid request: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateEntityException(DuplicateEntityException e) {
        log.error("Duplicate entity: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.error("Invalid credentials: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("JSON parse error: {}", e.getMessage());

        String message = "잘못된 요청 형식입니다.";
        if (e.getMessage().contains("OffsetDateTime")) {
            message = "날짜 형식이 올바르지 않습니다. ISO-8601 형식을 사용해주세요 (예: 2025-01-10T10:00:00Z)";
        }

        ApiResponse<?> apiResponse = ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), message);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bean Validation (@Valid) 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);

        String message = "입력값 검증에 실패했습니다: " + errors.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.joining(", "));

        ApiResponse<?> apiResponse = ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), message);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 제약 조건(Constraint) 위반 시 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException e) {
        String errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        log.error("Constraint violation: {}", errors);

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                "입력값 제약 조건 위반: " + errors
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 파일 크기 초과 시 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("File size exceeds limit: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다."
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Multipart 요청 처리 실패 시
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<?>> handleMultipartException(MultipartException e) {
        log.error("Multipart request error: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                "파일 업로드 처리 중 오류가 발생했습니다."
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * JSON 처리 오류 시
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON processing error: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                "JSON 데이터 처리 중 오류가 발생했습니다."
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 접근 권한 거부 시 (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.FORBIDDEN.value(),
                "접근 권한이 없습니다."
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 지원하지 않는 HTTP 메서드 요청 시
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("Method not supported: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "지원하지 않는 HTTP 메서드입니다: " + e.getMethod()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 그 외 모든 예외 처리 (Fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        // 어떤 예외가 발생했는지 서버 로그에 상세히 기록합니다.
        log.error("Unhandled exception occurred:", e);

        ApiResponse<?> apiResponse = ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다.");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
