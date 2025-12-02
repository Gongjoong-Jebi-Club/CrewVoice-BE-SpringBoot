/**
 * Spring 설정 클래스 패키지
 *
 * <p>애플리케이션의 각종 설정을 담당하는 Configuration 클래스들을 포함합니다.
 *
 * <p><b>현재 설정:</b>
 * <ul>
 *   <li>{@link org.skala.crewvoice.config.JpaConfig} - JPA Auditing 설정</li>
 *   <li>CorsConfig - CORS 설정 (이미 존재)</li>
 *   <li>SwaggerConfig - Swagger API 문서 설정 (이미 존재)</li>
 *   <li>RedisConfig - Redis 설정 (이미 존재)</li>
 * </ul>
 *
 * <p><b>설정 클래스 작성 규칙:</b>
 * <ul>
 *   <li>@Configuration 어노테이션 필수</li>
 *   <li>클래스명은 "기능명 + Config" 형식</li>
 *   <li>@Bean 메서드로 필요한 빈 등록</li>
 *   <li>환경 변수는 @Value 또는 @ConfigurationProperties 사용</li>
 * </ul>
 *
 * <p><b>설정 클래스 예시:</b>
 * <pre>
 * {@code
 * @Configuration
 * public class S3Config {
 *
 *     @Value("${aws.s3.bucket}")
 *     private String bucketName;
 *
 *     @Value("${aws.region}")
 *     private String region;
 *
 *     @Bean
 *     public AmazonS3 amazonS3() {
 *         return AmazonS3ClientBuilder
 *             .standard()
 *             .withRegion(region)
 *             .build();
 *     }
 * }
 * }
 * </pre>
 *
 * <p><b>이 패키지에 추가할 수 있는 설정:</b>
 * <ul>
 *   <li>S3Config.java - AWS S3 클라이언트 설정</li>
 *   <li>SecurityConfig.java - Spring Security 설정 (필요시)</li>
 *   <li>WebConfig.java - Web MVC 설정 (필요시)</li>
 *   <li>AsyncConfig.java - 비동기 처리 설정 (필요시)</li>
 *   <li>CacheConfig.java - 캐시 설정 (필요시)</li>
 * </ul>
 *
 * <p><b>주의사항:</b>
 * <ul>
 *   <li>설정 클래스는 가능한 한 분리하여 관리 (단일 책임 원칙)</li>
 *   <li>민감한 정보는 application.yml이나 환경 변수로 관리</li>
 *   <li>Profile별 설정이 필요한 경우 @Profile 어노테이션 활용</li>
 * </ul>
 *
 * @see org.skala.crewvoice.config.JpaConfig
 */
package org.skala.crewvoice.config;
