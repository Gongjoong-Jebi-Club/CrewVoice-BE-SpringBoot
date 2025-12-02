package org.skala.crewvoice.common;

/**
 * 애플리케이션 전역에서 사용되는 상수 정의
 *
 * <p>프로젝트 전반에 걸쳐 사용되는 공통 상수를 한 곳에 모아 관리합니다.
 * 인스턴스화를 방지하기 위해 private 생성자를 사용합니다.
 *
 * <p><b>상수 카테고리:</b>
 * <ul>
 *   <li>File - 파일 업로드 관련 상수</li>
 *   <li>Pagination - 페이지네이션 관련 상수</li>
 *   <li>Cache - 캐시 관련 상수</li>
 *   <li>S3 - AWS S3 디렉토리 경로 상수</li>
 * </ul>
 *
 * <p><b>사용 예시:</b>
 * <pre>
 * {@code
 * // 파일 크기 검증
 * if (file.getSize() > Constants.MAX_FILE_SIZE) {
 *     throw new InvalidRequestException(ErrorCode.INVALID_INPUT_VALUE);
 * }
 *
 * // 페이지네이션 기본값
 * int pageSize = request.getPageSize() != null
 *     ? request.getPageSize()
 *     : Constants.DEFAULT_PAGE_SIZE;
 *
 * // S3 경로 생성
 * String s3Key = Constants.S3_AUDIO_DIRECTORY + fileName;
 * }
 * </pre>
 *
 * <p><b>새로운 상수 추가 방법:</b>
 * <pre>
 * {@code
 * // 1. 적절한 카테고리에 상수 추가
 * public static final 타입 상수명 = 값;
 *
 * // 2. 새로운 카테고리가 필요한 경우 주석으로 구분
 * // NewCategory
 * public static final String NEW_CONSTANT = "value";
 * }
 * </pre>
 */
public final class Constants {

    /**
     * private 생성자 - 인스턴스화 방지
     */
    private Constants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // File - 파일 업로드 관련 상수
    /** 최대 파일 크기 (10MB) */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /** 허용되는 오디오 파일 MIME 타입 */
    public static final String[] ALLOWED_AUDIO_TYPES = {"audio/mpeg", "audio/wav", "audio/mp3"};

    // Pagination - 페이지네이션 관련 상수
    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 최대 페이지 크기 */
    public static final int MAX_PAGE_SIZE = 100;

    // Cache - 캐시 관련 상수
    /** 캐시 만료 시간 (1시간 = 3600초) */
    public static final int CACHE_EXPIRE_SECONDS = 3600; // 1 hour

    // S3 - AWS S3 디렉토리 경로
    /** S3 오디오 파일 저장 디렉토리 */
    public static final String S3_AUDIO_DIRECTORY = "audios/";

    /** S3 스크립트 파일 저장 디렉토리 */
    public static final String S3_SCRIPT_DIRECTORY = "scripts/";
}
