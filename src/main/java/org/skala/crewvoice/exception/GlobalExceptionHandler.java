package org.skala.crewvoice.exception;

import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.dto.response.ApiResponse;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        // 어떤 예외가 발생했는지 서버 로그에 상세히 기록합니다.
        log.error("Unhandled exception occurred:", e);

        ApiResponse<?> apiResponse = ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다.");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
