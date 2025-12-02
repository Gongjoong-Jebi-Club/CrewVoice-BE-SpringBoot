package org.skala.crewvoice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 모든 API 응답에 사용되는 공통 응답 형식
 *
 * <p>일관된 API 응답 구조를 제공하여 클라이언트의 응답 처리를 단순화합니다.
 * null 값은 JSON 응답에서 자동으로 제외됩니다.
 *
 * <p><b>응답 구조:</b>
 * <pre>
 * {
 *   "code": 200,
 *   "message": "요청이 성공했습니다.",
 *   "data": { ... }        // 성공 시 데이터 (optional)
 * }
 * </pre>
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 1. 데이터와 함께 성공 응답
 * return ApiResponse.success(userDto);
 *
 * // 2. 메시지와 함께 성공 응답
 * return ApiResponse.success(userDto, "사용자 생성 완료");
 *
 * // 3. 데이터 없이 성공 응답 (예: 삭제)
 * return ApiResponse.success();
 *
 * // 4. 에러 응답
 * return ApiResponse.error(401, "인증에 실패했습니다");
 * }
 * </pre>
 *
 * @param <T> 응답 데이터의 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** HTTP 상태 코드 */
    private final int code;

    /** 응답 메시지 */
    private final String message;

    /** 응답 데이터 */
    private final T data;

    /**
     * 데이터와 함께 성공 응답을 생성합니다 (200 OK).
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청이 성공했습니다.", data);
    }

    /**
     * 데이터와 메시지를 포함한 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param message 커스텀 메시지
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 데이터 없이 성공 응답을 생성합니다.
     * 주로 삭제, 업데이트 등 반환 데이터가 없는 경우 사용합니다.
     *
     * @return 성공 응답
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "요청이 성공했습니다.", null);
    }

    /**
     * 에러 응답을 생성합니다.
     *
     * @param code HTTP 상태 코드
     * @param message 에러 메시지
     * @return 실패 응답
     */
    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 에러 메시지와 데이터를 포함한 실패 응답을 생성합니다.
     *
     * @param code HTTP 상태 코드
     * @param message 에러 메시지
     * @param data 에러 관련 데이터
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /**
     * 실패 응답을 생성합니다 (error의 alias).
     *
     * @param code HTTP 상태 코드
     * @param message 에러 메시지
     * @return 실패 응답
     */
    public static ApiResponse<Void> failure(int code, String message) {
        return error(code, message);
    }
}