/**
 * Data Transfer Object (DTO) 패키지
 *
 * <p>계층 간 데이터 전달을 위한 DTO 클래스들을 포함합니다.
 * Entity를 직접 노출하지 않고 DTO를 통해 필요한 데이터만 전달합니다.
 *
 * <p><b>DTO 사용 목적:</b>
 * <ul>
 *   <li>Entity와 API 스펙의 분리 (Entity 변경이 API에 영향을 주지 않음)</li>
 *   <li>불필요한 데이터 노출 방지 (보안)</li>
 *   <li>순환참조 방지 (Entity 간 양방향 참조 시)</li>
 *   <li>API 응답 커스터마이징 (여러 Entity의 데이터 조합 가능)</li>
 * </ul>
 *
 * <p><b>DTO 작성 규칙:</b>
 * <ul>
 *   <li>request 패키지: 클라이언트 → 서버 요청 DTO</li>
 *   <li>response 패키지: 서버 → 클라이언트 응답 DTO</li>
 *   <li>Lombok의 @Getter, @Builder, @NoArgsConstructor, @AllArgsConstructor 활용</li>
 *   <li>요청 DTO는 @Valid 검증을 위한 javax.validation 어노테이션 추가</li>
 *   <li>응답 DTO는 Entity로부터 변환하는 정적 팩토리 메서드 제공 (from, of 등)</li>
 * </ul>
 *
 * <p><b>하위 패키지:</b>
 * <ul>
 *   <li>{@link org.skala.crewvoice.dto.request} - 요청 DTO</li>
 *   <li>{@link org.skala.crewvoice.dto.response} - 응답 DTO</li>
 * </ul>
 *
 * @see org.skala.crewvoice.dto.request
 * @see org.skala.crewvoice.dto.response
 */
package org.skala.crewvoice.dto;
