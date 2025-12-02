package org.skala.crewvoice.exception.custom;

import lombok.Getter;
import org.skala.crewvoice.exception.ErrorCode;

/**
 * 잘못된 요청이 들어왔을 때 발생하는 예외
 *
 * <p>비즈니스 로직 규칙을 위반하거나, 잘못된 입력값이 전달된 경우 사용합니다.
 * GlobalExceptionHandler에서 이 예외를 캐치하여 적절한 HTTP 응답으로 변환합니다.
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 1. 기본 에러 메시지 사용
 * if (!allowedFileTypes.contains(fileType)) {
 *     throw new InvalidRequestException(ErrorCode.INVALID_FILE_TYPE);
 * }
 *
 * // 2. 커스텀 메시지 사용
 * if (file.getSize() > MAX_FILE_SIZE) {
 *     throw new InvalidRequestException(
 *         "파일 크기는 " + MAX_FILE_SIZE + "바이트를 초과할 수 없습니다",
 *         ErrorCode.INVALID_INPUT_VALUE
 *     );
 * }
 *
 * // 3. 비즈니스 로직 검증
 * if (user.isDeleted()) {
 *     throw new InvalidRequestException(
 *         "삭제된 사용자는 수정할 수 없습니다",
 *         ErrorCode.INVALID_INPUT_VALUE
 *     );
 * }
 * }
 * </pre>
 *
 * @see org.skala.crewvoice.exception.GlobalExceptionHandler
 * @see ErrorCode
 */
@Getter
public class InvalidRequestException extends RuntimeException {

    /** 에러 코드 */
    private final ErrorCode errorCode;

    /**
     * ErrorCode의 기본 메시지를 사용하는 생성자
     *
     * @param errorCode 에러 코드
     */
    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 커스텀 에러 메시지
     * @param errorCode 에러 코드
     */
    public InvalidRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}