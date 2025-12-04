package org.skala.crewvoice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전역에서 사용되는 에러 코드 정의
 *
 * <p>각 에러 코드는 HTTP 상태 코드, 커스텀 코드, 메시지를 포함합니다.
 * GlobalExceptionHandler에서 이 에러 코드를 사용하여 일관된 에러 응답을 생성합니다.
 *
 * <p><b>코드 체계:</b>
 * <ul>
 *   <li>C### : Common (공통 에러)</li>
 *   <li>U### : User (사용자 관련)</li>
 *   <li>E### : Evaluation (평가 관련)</li>
 *   <li>S### : Script (스크립트 관련)</li>
 *   <li>F### : File (파일 관련)</li>
 * </ul>
 *
 * <p><b>에러 코드 추가 방법:</b>
 * <pre>
 * {@code
 * // 1. 적절한 카테고리에 에러 코드 추가
 * NEW_ERROR_CODE(HttpStatus.상태코드, "카테고리코드", "에러 메시지"),
 *
 * // 2. 커스텀 예외에서 사용
 * throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
 * }
 * </pre>
 *
 * @see GlobalExceptionHandler
 * @see ErrorResponse
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common - 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method not allowed"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "Internal server error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "Invalid type value"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "Entity not found"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access denied"),

    // User - 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "U002", "Duplicate user"),

    // Evaluation - 평가 관련 에러
    EVALUATION_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "Evaluation not found"),

    // Script - 스크립트 관련 에러
    SCRIPT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "Script not found"),

    // File - 파일 관련 에러
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "File upload failed"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F002", "File delete failed"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F003", "Invalid file type"),

    // Crew - 평가 대상자 관련 에러
    CREW_INFO_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C007", "Failed to fetch crew information"),

    // 녹음 파일 다운로드 관련 에러
    INVALID_STEP_PARAMETER(HttpStatus.BAD_REQUEST, "R001", "step은 2 또는 3만 가능합니다."),
    STEP2_NO_QUESTION_NEEDED(HttpStatus.BAD_REQUEST, "R002", "STEP2는 question 파라미터가 필요하지 않습니다."),
    STEP3_QUESTION_REQUIRED(HttpStatus.BAD_REQUEST, "R003", "STEP3는 question 파라미터가 필수입니다. (1, 2, 3 중 하나)"),
    INVALID_QUESTION_NUMBER(HttpStatus.BAD_REQUEST, "R004", "question은 1~3만 가능합니다."),
    INVALID_LANGUAGE_PARAMETER(HttpStatus.BAD_REQUEST, "R005", "language는 'kor' 또는 'eng'만 가능합니다."),
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "R006", "해당 사용자의 평가 제출 내역을 찾을 수 없습니다."),
    RECORDING_FILE_NOT_UPLOADED(HttpStatus.BAD_REQUEST, "R007", "요청한 언어의 음성 파일이 업로드되지 않았습니다."),
    RECORDING_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "R008", "S3에 저장된 음성 파일을 찾을 수 없습니다."),
    S3_PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R009", "S3 다운로드 URL 생성에 실패했습니다.");

    // Question - 문제 관련 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Q001", "Question not found");

    /** HTTP 상태 코드 */
    private final HttpStatus status;

    /** 커스텀 에러 코드 (예: U001, E001) */
    private final String code;

    /** 에러 메시지 */
    private final String message;
}