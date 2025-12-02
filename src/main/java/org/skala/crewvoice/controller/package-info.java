/**
 * REST API 컨트롤러 패키지
 *
 * <p>클라이언트의 HTTP 요청을 받아 처리하고 응답을 반환하는 Controller들을 포함합니다.
 *
 * <p><b>Controller 작성 규칙:</b>
 * <ul>
 *   <li>@RestController 어노테이션 필수</li>
 *   <li>@RequestMapping으로 공통 경로 설정</li>
 *   <li>응답은 {@link org.skala.crewvoice.common.ApiResponse}로 감싸서 반환</li>
 *   <li>요청 데이터는 Request DTO로, 응답 데이터는 Response DTO로 처리</li>
 *   <li>@Valid로 요청 데이터 유효성 검증</li>
 *   <li>Swagger 문서화 어노테이션 추가 (@Operation, @Tag 등)</li>
 * </ul>
 *
 * <p><b>Controller 예시:</b>
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/api/users")
 * @RequiredArgsConstructor
 * @Tag(name = "User", description = "사용자 관리 API")
 * public class UserController {
 *
 *     private final UserService userService;
 *
 *     @GetMapping("/{id}")
 *     @Operation(summary = "사용자 조회", description = "ID로 사용자를 조회합니다")
 *     public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
 *         UserResponse user = userService.getUser(id);
 *         return ApiResponse.success(user);
 *     }
 *
 *     @PostMapping
 *     @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
 *     public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
 *         UserResponse user = userService.createUser(request);
 *         return ApiResponse.success(user, "사용자가 생성되었습니다");
 *     }
 *
 *     @DeleteMapping("/{id}")
 *     @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다")
 *     public ApiResponse<Void> deleteUser(@PathVariable Long id) {
 *         userService.deleteUser(id);
 *         return ApiResponse.success();
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>UserController.java - 사용자 API</li>
 *   <li>EvaluationController.java - 평가 API</li>
 *   <li>ScriptController.java - 스크립트 API</li>
 *   <li>HealthController.java - 헬스체크 API (이미 존재)</li>
 * </ul>
 *
 * @see org.skala.crewvoice.service
 * @see org.skala.crewvoice.common.ApiResponse
 */
package org.skala.crewvoice.controller;
