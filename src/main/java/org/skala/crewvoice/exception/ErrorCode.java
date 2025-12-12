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
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 가입된 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U004", "잘못된 비밀번호입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "U005", "잘못된 인증 정보입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "U006", "이메일 또는 비밀번호가 잘못되었습니다."),

    // Auth - 인증 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    INVALID_AUTH_HEADER(HttpStatus.BAD_REQUEST, "A002", "Authorization 헤더가 없거나 형식이 잘못되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "저장된 Refresh Token이 없습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "Refresh Token이 만료되었습니다. 다시 로그인해주세요."),

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
    S3_PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R009", "S3 다운로드 URL 생성에 실패했습니다."),
    INVALID_S3_URL(HttpStatus.INTERNAL_SERVER_ERROR, "R010", "S3 URL 형식이 올바르지 않습니다."),

    // Question - 문제 관련 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Q001", "문제를 찾을 수 없습니다."),
    QUESTION_BANK_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Q002", "문제은행 정보를 불러오지 못했습니다."),
    INVALID_QUESTION_STEP(HttpStatus.BAD_REQUEST, "Q003", "유효하지 않은 STEP입니다."),
    ALREADY_DELETED_QUESTION(HttpStatus.BAD_REQUEST, "Q004", "이미 삭제된 문제입니다."),
    NO_QUESTIONS_AVAILABLE(HttpStatus.NOT_FOUND, "Q005", "사용 가능한 문제가 없습니다."),
    QUESTION_IN_USE(HttpStatus.CONFLICT, "Q006", "현재 사용 중인 문제는 삭제할 수 없습니다."),

    // Submission - 제출 관련 에러
    DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "SUB001", "이미 제출한 답변입니다."),
    INVALID_ANSWER_OPTION(HttpStatus.BAD_REQUEST, "SUB002", "유효하지 않은 답변입니다."),

    // Audio File - 오디오 파일 관련 에러
    AUDIO_FILE_INVALID(HttpStatus.BAD_REQUEST, "AF001", "유효하지 않은 오디오 파일입니다."),
    AUDIO_FILE_EMPTY(HttpStatus.BAD_REQUEST, "AF002", "빈 파일은 업로드할 수 없습니다."),
    AUDIO_FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "AF003", "파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다."),
    AUDIO_FILE_WRONG_FORMAT(HttpStatus.BAD_REQUEST, "AF004", "WAV 형식의 오디오 파일만 업로드 가능합니다."),

    // S3 - S3 관련 에러
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3001", "파일 업로드에 실패했습니다."),
    S3_UPLOAD_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "S3002", "파일 업로드 시간이 초과되었습니다."),
    S3_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "S3003", "스토리지 서비스에 연결할 수 없습니다."),

    // FastAPI Integration - FastAPI 연동 에러
    FASTAPI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "API001", "평가 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요."),
    FASTAPI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "API002", "평가 처리 시간이 초과되었습니다."),
    FASTAPI_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "API003", "평가 서비스로부터 잘못된 응답을 받았습니다."),

    // Evaluation Status - 평가 상태 관련 에러
    EVALUATION_NOT_STARTED_YET(HttpStatus.BAD_REQUEST, "EV001", "평가 시작 기간이 아닙니다."),
    EVALUATION_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "EV002", "평가 종료 기간이 지났습니다."),
    EVALUATION_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "EV003", "이미 진행 중인 평가입니다."),
    EVALUATION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "EV004", "평가가 아직 완료되지 않았습니다."),
    EVALUATION_HAS_NO_CREWS(HttpStatus.NOT_FOUND, "EV005", "해당 평가에 배정된 평가 대상자가 없습니다."),

    // Result - 결과 관련 에러
    NO_SUBMISSION_FOUND(HttpStatus.NOT_FOUND, "RES001", "제출 내역이 없습니다."),
    STEP_NOT_COMPLETED(HttpStatus.NOT_FOUND, "RES002", "해당 단계를 완료하지 않았습니다."),
    RESULT_NOT_AVAILABLE_YET(HttpStatus.BAD_REQUEST, "RES003", "아직 결과를 확인할 수 없습니다.");

    /** HTTP 상태 코드 */
    private final HttpStatus status;

    /** 커스텀 에러 코드 (예: U001, E001) */
    private final String code;

    /** 에러 메시지 */
    private final String message;
}