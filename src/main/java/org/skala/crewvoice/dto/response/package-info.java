/**
 * 응답 DTO 패키지
 *
 * <p>서버에서 클라이언트로 전달되는 응답 데이터를 담는 DTO들을 포함합니다.
 *
 * <p><b>응답 DTO 작성 규칙:</b>
 * <ul>
 *   <li>클래스명은 "도메인명 + Response" 형식</li>
 *   <li>Entity로부터 변환하는 정적 팩토리 메서드 제공 (from, of 등)</li>
 *   <li>불필요한 정보는 포함하지 않음 (비밀번호 등)</li>
 *   <li>Swagger 문서화를 위한 @Schema 어노테이션 추가 권장</li>
 *   <li>날짜는 ISO-8601 형식 또는 원하는 형식으로 변환</li>
 * </ul>
 *
 * <p><b>응답 DTO 예시:</b>
 * <pre>
 * {@code
 * @Getter
 * @NoArgsConstructor
 * @AllArgsConstructor
 * @Builder
 * public class UserResponse {
 *
 *     @Schema(description = "사용자 ID", example = "1")
 *     private Long id;
 *
 *     @Schema(description = "사용자 이름", example = "홍길동")
 *     private String name;
 *
 *     @Schema(description = "이메일 주소", example = "user@example.com")
 *     private String email;
 *
 *     @Schema(description = "생성일시", example = "2024-01-01T12:00:00")
 *     private LocalDateTime createdAt;
 *
 *     // Entity → Response 변환 메서드
 *     public static UserResponse from(User user) {
 *         return UserResponse.builder()
 *             .id(user.getId())
 *             .name(user.getName())
 *             .email(user.getEmail())
 *             .createdAt(user.getCreatedAt())
 *             .build();
 *     }
 *
 *     // 여러 Entity를 조합하는 경우
 *     public static UserResponse of(User user, List<Evaluation> evaluations) {
 *         return UserResponse.builder()
 *             .id(user.getId())
 *             .name(user.getName())
 *             .email(user.getEmail())
 *             .evaluationCount(evaluations.size())
 *             .build();
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>UserResponse.java - 사용자 응답</li>
 *   <li>EvaluationResponse.java - 평가 응답</li>
 *   <li>ScriptResponse.java - 스크립트 응답</li>
 *   <li>EvaluationDetailResponse.java - 평가 상세 응답</li>
 * </ul>
 *
 * @see org.skala.crewvoice.dto.request
 */
package org.skala.crewvoice.dto.response;
