/**
 * 유틸리티 클래스 패키지
 *
 * <p>특정 기능을 수행하는 헬퍼 유틸리티 클래스들을 포함합니다.
 *
 * <p><b>유틸리티 클래스 작성 규칙:</b>
 * <ul>
 *   <li>클래스명은 "기능명 + Util" 형식</li>
 *   <li>final class로 선언하여 상속 방지</li>
 *   <li>private 생성자로 인스턴스화 방지</li>
 *   <li>모든 메서드는 static으로 작성</li>
 *   <li>상태를 가지지 않음 (stateless)</li>
 * </ul>
 *
 * <p><b>유틸리티 클래스 예시:</b>
 * <pre>
 * {@code
 * public final class S3Util {
 *
 *     private S3Util() {
 *         throw new AssertionError("Cannot instantiate utility class");
 *     }
 *
 *     public static String uploadFile(MultipartFile file, String directory) {
 *         // S3 업로드 로직
 *         String fileName = generateFileName(file.getOriginalFilename());
 *         String s3Key = directory + fileName;
 *         // upload to S3...
 *         return s3Key;
 *     }
 *
 *     public static void deleteFile(String s3Key) {
 *         // S3 삭제 로직
 *     }
 *
 *     private static String generateFileName(String originalName) {
 *         return UUID.randomUUID() + "_" + originalName;
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>S3Util.java - AWS S3 파일 업로드/삭제 유틸</li>
 *   <li>AudioUtil.java - 오디오 파일 처리 유틸</li>
 *   <li>기타 특정 기능을 수행하는 유틸리티 클래스들</li>
 * </ul>
 *
 * <p><b>Service vs Util 구분:</b>
 * <ul>
 *   <li>Service: 비즈니스 로직, 트랜잭션 관리, 의존성 주입 필요</li>
 *   <li>Util: 단순 기능, 상태 없음, static 메서드, 의존성 없음</li>
 * </ul>
 *
 * @see org.skala.crewvoice.common
 */
package org.skala.crewvoice.util;
