/**
 * 커스텀 예외 클래스 패키지
 *
 * <p>비즈니스 로직에서 발생하는 도메인 특화 예외들을 포함합니다.
 *
 * <p><b>기본 제공 커스텀 예외:</b>
 * <ul>
 *   <li>{@link org.skala.crewvoice.exception.custom.EntityNotFoundException}
 *       - 엔티티를 찾을 수 없을 때 발생</li>
 *   <li>{@link org.skala.crewvoice.exception.custom.InvalidRequestException}
 *       - 잘못된 요청일 때 발생</li>
 * </ul>
 *
 * <p><b>커스텀 예외 작성 규칙:</b>
 * <ul>
 *   <li>RuntimeException을 상속받아야 합니다</li>
 *   <li>ErrorCode를 필드로 가지고 있어야 합니다</li>
 *   <li>기본 메시지와 커스텀 메시지를 모두 지원하는 생성자 제공</li>
 *   <li>@Getter 어노테이션으로 errorCode getter 제공</li>
 * </ul>
 *
 * <p><b>커스텀 예외 작성 예시:</b>
 * <pre>
 * {@code
 * @Getter
 * public class FileProcessingException extends RuntimeException {
 *
 *     private final ErrorCode errorCode;
 *
 *     public FileProcessingException(ErrorCode errorCode) {
 *         super(errorCode.getMessage());
 *         this.errorCode = errorCode;
 *     }
 *
 *     public FileProcessingException(String message, ErrorCode errorCode) {
 *         super(message);
 *         this.errorCode = errorCode;
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 1. 기본 메시지 사용
 * throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
 *
 * // 2. 커스텀 메시지 사용
 * throw new InvalidRequestException(
 *     "파일 크기는 10MB를 초과할 수 없습니다",
 *     ErrorCode.INVALID_INPUT_VALUE
 * );
 * }
 * </pre>
 *
 * <p><b>새로운 커스텀 예외 추가 후 처리:</b>
 * <ol>
 *   <li>ErrorCode enum에 관련 에러 코드 추가</li>
 *   <li>GlobalExceptionHandler에 @ExceptionHandler 메서드 추가</li>
 * </ol>
 *
 * @see org.skala.crewvoice.exception.ErrorCode
 * @see org.skala.crewvoice.exception.GlobalExceptionHandler
 */
package org.skala.crewvoice.exception.custom;
