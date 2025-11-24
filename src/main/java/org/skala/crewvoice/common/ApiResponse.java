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
 *   "success": true/false,
 *   "data": { ... },        // 성공 시 데이터 (optional)
 *   "message": "메시지"      // 추가 메시지 (optional)
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
 * return ApiResponse.error("잘못된 요청입니다");
 * }
 * </pre>
 *
 * @param <T> 응답 데이터의 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** 요청 성공 여부 */
    private final boolean success;

    /** 응답 데이터 */
    private final T data;

    /** 추가 메시지 */
    private final String message;

    /**
     * 데이터와 함께 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 데이터와 메시지를 포함한 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param message 추가 메시지
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    /**
     * 데이터 없이 성공 응답을 생성합니다.
     * 주로 삭제, 업데이트 등 반환 데이터가 없는 경우 사용합니다.
     *
     * @return 성공 응답
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    /**
     * 에러 메시지를 포함한 실패 응답을 생성합니다.
     *
     * @param message 에러 메시지
     * @return 실패 응답
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    /**
     * 에러 메시지와 데이터를 포함한 실패 응답을 생성합니다.
     * 주로 유효성 검증 실패 시 검증 결과를 함께 반환할 때 사용합니다.
     *
     * @param message 에러 메시지
     * @param data 에러 관련 데이터
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, data, message);
    }
}