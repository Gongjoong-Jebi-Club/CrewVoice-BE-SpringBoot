/**
 * 공통 유틸리티 및 공용 클래스 패키지
 *
 * <p>애플리케이션 전역에서 사용되는 공통 클래스들을 포함합니다.
 *
 * <p><b>패키지 구성:</b>
 * <ul>
 *   <li>{@link org.skala.crewvoice.common.ApiResponse} - 통일된 API 응답 형식</li>
 *   <li>{@link org.skala.crewvoice.common.Constants} - 애플리케이션 전역 상수</li>
 * </ul>
 *
 * <p><b>ApiResponse 사용:</b>
 * <pre>
 * {@code
 * // Controller에서 응답 반환
 * @GetMapping("/users/{id}")
 * public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
 *     UserResponse user = userService.getUser(id);
 *     return ApiResponse.success(user);
 * }
 *
 * // 클라이언트가 받는 JSON
 * {
 *   "success": true,
 *   "data": {
 *     "id": 1,
 *     "name": "홍길동"
 *   },
 *   "message": null
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가할 수 있는 파일:</b>
 * <ul>
 *   <li>공통 응답 형식 클래스</li>
 *   <li>애플리케이션 전역 상수</li>
 *   <li>공통 유틸리티 클래스 (단순한 것들)</li>
 * </ul>
 *
 * <p><b>주의:</b> 복잡한 유틸리티는 {@link org.skala.crewvoice.util} 패키지에 작성하세요.
 *
 * @see org.skala.crewvoice.common.ApiResponse
 * @see org.skala.crewvoice.common.Constants
 * @see org.skala.crewvoice.util
 */
package org.skala.crewvoice.common;
