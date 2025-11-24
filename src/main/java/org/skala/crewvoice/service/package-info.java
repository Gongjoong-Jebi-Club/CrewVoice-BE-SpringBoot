/**
 * 비즈니스 로직을 처리하는 Service 계층 패키지
 *
 * <p>Controller와 Repository 사이에서 비즈니스 로직을 수행합니다.
 * 트랜잭션 관리, 데이터 가공, 비즈니스 규칙 검증 등을 담당합니다.
 *
 * <p><b>Service 작성 규칙:</b>
 * <ul>
 *   <li>@Service 어노테이션 필수</li>
 *   <li>@Transactional 어노테이션으로 트랜잭션 관리</li>
 *   <li>Controller에서 직접 Repository를 호출하지 않고 Service를 통해 접근</li>
 *   <li>Entity를 직접 반환하지 않고 DTO로 변환하여 반환</li>
 *   <li>생성자 주입 방식 사용 (Lombok @RequiredArgsConstructor 권장)</li>
 * </ul>
 *
 * <p><b>Service 예시:</b>
 * <pre>
 * {@code
 * @Service
 * @RequiredArgsConstructor
 * @Transactional(readOnly = true)
 * public class UserService {
 *
 *     private final UserRepository userRepository;
 *
 *     // 조회 메서드 (readOnly = true)
 *     public UserResponse getUser(Long id) {
 *         User user = userRepository.findById(id)
 *             .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
 *         return UserResponse.from(user);
 *     }
 *
 *     // 변경 메서드 (readOnly = false)
 *     @Transactional
 *     public UserResponse createUser(UserCreateRequest request) {
 *         // 중복 검증
 *         if (userRepository.existsByEmail(request.getEmail())) {
 *             throw new InvalidRequestException(ErrorCode.DUPLICATE_USER);
 *         }
 *
 *         User user = User.builder()
 *             .name(request.getName())
 *             .email(request.getEmail())
 *             .build();
 *
 *         User savedUser = userRepository.save(user);
 *         return UserResponse.from(savedUser);
 *     }
 *
 *     @Transactional
 *     public void deleteUser(Long id) {
 *         User user = userRepository.findById(id)
 *             .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
 *         userRepository.delete(user);
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가해야 할 파일:</b>
 * <ul>
 *   <li>UserService.java - 사용자 비즈니스 로직</li>
 *   <li>EvaluationService.java - 평가 비즈니스 로직</li>
 *   <li>ScriptService.java - 스크립트 비즈니스 로직</li>
 *   <li>AiEvaluationService.java - AI 평가 연동 로직</li>
 * </ul>
 *
 * @see org.skala.crewvoice.controller
 * @see org.skala.crewvoice.repository
 */
package org.skala.crewvoice.service;
