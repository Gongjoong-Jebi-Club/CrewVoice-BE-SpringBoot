package org.skala.crewvoice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 에러 발생 시 클라이언트에게 전달되는 응답 형식
 *
 * <p>모든 예외 상황에 대해 일관된 에러 응답 구조를 제공합니다.
 * GlobalExceptionHandler에서 이 클래스를 사용하여 에러 응답을 생성합니다.
 *
 * <p><b>응답 구조:</b>
 * <pre>
 * {
 *   "code": "U001",                    // 에러 코드
 *   "message": "User not found",       // 에러 메시지
 *   "status": 404,                     // HTTP 상태 코드
 *   "timestamp": "2024-01-01T12:00:00", // 에러 발생 시각
 *   "errors": [                        // 유효성 검증 실패 시 (optional)
 *     {
 *       "field": "email",
 *       "value": "invalid",
 *       "reason": "올바른 이메일 형식이 아닙니다"
 *     }
 *   ]
 * }
 * </pre>
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 1. 기본 에러 응답
 * ErrorResponse response = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
 *
 * // 2. 커스텀 메시지를 포함한 에러 응답
 * ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, "이메일은 필수입니다");
 *
 * // 3. 유효성 검증 실패 응답 (BindingResult 포함)
 * ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, bindingResult);
 * }
 * </pre>
 *
 * @see ErrorCode
 * @see GlobalExceptionHandler
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    /** 에러 코드 (예: U001, E001) */
    private final String code;

    /** 에러 메시지 */
    private final String message;

    /** HTTP 상태 코드 */
    private final int status;

    /** 에러 발생 시각 */
    private final LocalDateTime timestamp;

    /** 필드 유효성 검증 에러 목록 (유효성 검증 실패 시에만 포함) */
    private final List<FieldErrorDetail> errors;

    /**
     * ErrorCode로부터 기본 에러 응답을 생성합니다.
     *
     * @param errorCode 에러 코드
     * @return 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                LocalDateTime.now(),
                null
        );
    }

    /**
     * ErrorCode와 커스텀 메시지로 에러 응답을 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param message 커스텀 에러 메시지
     * @return 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getCode(),
                message,
                errorCode.getStatus().value(),
                LocalDateTime.now(),
                null
        );
    }

    /**
     * 유효성 검증 실패 시 필드별 에러 정보를 포함한 에러 응답을 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param bindingResult Spring Validation의 검증 결과
     * @return 필드 에러 정보가 포함된 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                LocalDateTime.now(),
                FieldErrorDetail.of(bindingResult)
        );
    }

    /**
     * 필드 유효성 검증 실패 상세 정보
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldErrorDetail {
        /** 에러가 발생한 필드명 */
        private final String field;

        /** 거부된 값 */
        private final String value;

        /** 에러 사유 */
        private final String reason;

        /**
         * BindingResult로부터 필드 에러 목록을 생성합니다.
         *
         * @param bindingResult Spring Validation의 검증 결과
         * @return 필드 에러 상세 목록
         */
        private static List<FieldErrorDetail> of(BindingResult bindingResult) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldErrorDetail(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}