package org.skala.crewvoice.exception;

import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역 예외 처리 핸들러
 *
 * <p>모든 Controller에서 발생하는 예외를 통합적으로 처리하여
 * 일관된 에러 응답 형식을 제공합니다.
 *
 * <p><b>처리하는 예외 유형:</b>
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} - 요청 데이터 유효성 검증 실패</li>
 *   <li>{@link HttpRequestMethodNotSupportedException} - 지원하지 않는 HTTP 메서드</li>
 *   <li>{@link EntityNotFoundException} - 엔티티를 찾을 수 없음 (커스텀)</li>
 *   <li>{@link InvalidRequestException} - 잘못된 요청 (커스텀)</li>
 *   <li>{@link Exception} - 기타 예상하지 못한 모든 예외</li>
 * </ul>
 *
 * <p><b>예외 처리 흐름:</b>
 * <pre>
 * Controller에서 예외 발생
 *   ↓
 * GlobalExceptionHandler가 캐치
 *   ↓
 * 적절한 ErrorResponse 생성
 *   ↓
 * 클라이언트에게 일관된 형식으로 응답
 * </pre>
 *
 * <p><b>새로운 예외 핸들러 추가 방법:</b>
 * <pre>
 * {@code
 * @ExceptionHandler(YourCustomException.class)
 * protected ResponseEntity<ErrorResponse> handleYourCustomException(YourCustomException e) {
 *     log.error("YourCustomException", e);
 *     ErrorResponse response = ErrorResponse.of(ErrorCode.YOUR_ERROR_CODE);
 *     return new ResponseEntity<>(response, HttpStatus.YOUR_STATUS);
 * }
 * }
 * </pre>
 *
 * @see ErrorResponse
 * @see ErrorCode
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid, @Validated 어노테이션을 통한 유효성 검증 실패 처리
     *
     * @param e 유효성 검증 예외
     * @return 필드별 에러 정보가 포함된 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지원하지 않는 HTTP 메서드 요청 처리
     * 예: POST 엔드포인트에 GET 요청
     *
     * @param e HTTP 메서드 예외
     * @return 에러 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 엔티티를 찾을 수 없는 경우 처리
     * 예: 존재하지 않는 사용자 ID로 조회
     *
     * @param e 엔티티 미발견 예외
     * @return 에러 응답
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus().value()));
    }

    /**
     * 잘못된 요청 처리
     * 예: 비즈니스 로직 규칙 위반
     *
     * @param e 잘못된 요청 예외
     * @return 에러 응답
     */
    @ExceptionHandler(InvalidRequestException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e) {
        log.error("InvalidRequestException", e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus().value()));
    }

    /**
     * 처리되지 않은 모든 예외에 대한 최종 처리
     * 예상하지 못한 서버 에러 발생 시 500 Internal Server Error 응답
     *
     * @param e 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}