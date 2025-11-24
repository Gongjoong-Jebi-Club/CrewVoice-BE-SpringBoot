/**
 * 예외 처리 관련 패키지
 *
 * <p>애플리케이션 전역 예외 처리와 관련된 클래스들을 포함합니다.
 * 일관된 에러 응답 형식을 제공하고, 예외를 체계적으로 관리합니다.
 *
 * <p><b>패키지 구성:</b>
 * <ul>
 *   <li>{@link org.skala.crewvoice.exception.GlobalExceptionHandler} - 전역 예외 처리 핸들러</li>
 *   <li>{@link org.skala.crewvoice.exception.ErrorCode} - 에러 코드 정의</li>
 *   <li>{@link org.skala.crewvoice.exception.ErrorResponse} - 에러 응답 형식</li>
 *   <li>custom 패키지 - 커스텀 예외 클래스들</li>
 * </ul>
 *
 * <p><b>예외 처리 흐름:</b>
 * <pre>
 * 1. 비즈니스 로직에서 예외 발생
 *    throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
 *
 * 2. GlobalExceptionHandler가 예외를 캐치
 *
 * 3. ErrorResponse 생성 및 반환
 *    {
 *      "code": "U001",
 *      "message": "User not found",
 *      "status": 404,
 *      "timestamp": "2024-01-01T12:00:00"
 *    }
 * </pre>
 *
 * <p><b>새로운 에러 코드 추가 방법:</b>
 * <ol>
 *   <li>ErrorCode enum에 새로운 에러 코드 추가</li>
 *   <li>필요시 custom 패키지에 커스텀 예외 클래스 추가</li>
 *   <li>GlobalExceptionHandler에 해당 예외 핸들러 추가</li>
 * </ol>
 *
 * @see org.skala.crewvoice.exception.GlobalExceptionHandler
 * @see org.skala.crewvoice.exception.ErrorCode
 * @see org.skala.crewvoice.exception.ErrorResponse
 * @see org.skala.crewvoice.exception.custom
 */
package org.skala.crewvoice.exception;
