package org.skala.crewvoice.exception.custom;

import lombok.Getter;
import org.skala.crewvoice.exception.ErrorCode;

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 *
 * <p>데이터베이스에서 특정 엔티티를 조회했으나 존재하지 않는 경우 사용합니다.
 * GlobalExceptionHandler에서 이 예외를 캐치하여 적절한 HTTP 응답으로 변환합니다.
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 1. 기본 에러 메시지 사용
 * User user = userRepository.findById(userId)
 *     .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
 *
 * // 2. 커스텀 메시지 사용
 * Script script = scriptRepository.findById(scriptId)
 *     .orElseThrow(() -> new EntityNotFoundException(
 *         "ID " + scriptId + "에 해당하는 스크립트를 찾을 수 없습니다",
 *         ErrorCode.SCRIPT_NOT_FOUND
 *     ));
 * }
 * </pre>
 *
 * @see org.skala.crewvoice.exception.GlobalExceptionHandler
 * @see ErrorCode
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    /** 에러 코드 */
    private final ErrorCode errorCode;

    /**
     * ErrorCode의 기본 메시지를 사용하는 생성자
     *
     * @param errorCode 에러 코드
     */
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 커스텀 에러 메시지
     * @param errorCode 에러 코드
     */
    public EntityNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}