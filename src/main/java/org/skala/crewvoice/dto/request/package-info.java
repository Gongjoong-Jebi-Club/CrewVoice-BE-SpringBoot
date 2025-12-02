/**
 * 요청 DTO 패키지
 *
 * <p>클라이언트에서 서버로 전달되는 요청 데이터를 담는 DTO들을 포함합니다.
 *
 * <p><b>요청 DTO 작성 규칙:</b>
 * <ul>
 *   <li>클래스명은 "도메인명 + Create/Update/Search + Request" 형식</li>
 *   <li>javax.validation 어노테이션으로 유효성 검증 규칙 정의</li>
 *   <li>@NotNull, @NotBlank, @Size, @Email, @Pattern 등 활용</li>
 *   <li>Swagger 문서화를 위한 @Schema 어노테이션 추가 권장</li>
 * </ul>
 *
 * <p><b>요청 DTO 예시:</b>
 * <pre>
 * {@code
 * @Getter
 * @NoArgsConstructor
 * @AllArgsConstructor
 * @Builder
 * public class UserCreateRequest {
 *
 *     @NotBlank(message = "이름은 필수입니다")
 *     @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
 *     @Schema(description = "사용자 이름", example = "홍길동")
 *     private String name;
 *
 *     @NotBlank(message = "이메일은 필수입니다")
 *     @Email(message = "올바른 이메일 형식이 아닙니다")
 *     @Schema(description = "이메일 주소", example = "user@example.com")
 *     private String email;
 *
 *     @Min(value = 0, message = "나이는 0 이상이어야 합니다")
 *     @Schema(description = "나이", example = "25")
 *     private Integer age;
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>UserCreateRequest.java - 사용자 생성 요청</li>
 *   <li>UserUpdateRequest.java - 사용자 수정 요청</li>
 *   <li>EvaluationRequest.java - 평가 요청</li>
 *   <li>ScriptRequest.java - 스크립트 요청</li>
 * </ul>
 *
 * @see org.skala.crewvoice.dto.response
 */
package org.skala.crewvoice.dto.request;
