package org.skala.crewvoice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS S3 설정 (Spring Cloud AWS 자동 설정 + 수동 Presigner 추가)
 *
 * S3Client는 Spring Cloud AWS가 자동으로 빈 생성
 * S3Presigner만 수동으로 빈 생성 (자동 설정에 포함되지 않음)
 */
@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * S3Presigner 빈 생성 (Pre-signed URL 생성용)
     *
     * Spring Cloud AWS는 S3Client는 자동 생성하지만
     * S3Presigner는 제공하지 않으므로 수동 등록
     */
    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}