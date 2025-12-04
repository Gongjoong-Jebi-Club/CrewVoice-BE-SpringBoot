package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

/**
 * AWS S3 관련 서비스 (AWS SDK v2)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket:crewvoice-bucket}")
    private String bucketName;

    /**
     * S3 Object Key로 Pre-signed URL 생성 (AWS SDK v2)
     *
     * @param objectKey S3 Object Key
     * @param expirationSeconds 만료 시간 (초)
     * @return Pre-signed URL
     */
    public String generatePresignedUrl(String objectKey, int expirationSeconds) {
        log.info("Pre-signed URL 생성 시작 - Bucket: {}, Key: {}, Expiration: {}초",
                bucketName, objectKey, expirationSeconds);

        try {
            // GetObjectRequest 생성
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Pre-signed URL 요청 생성
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationSeconds))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // Pre-signed URL 생성
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.info("Pre-signed URL 생성 완료 - URL 길이: {}", presignedUrl.length());
            return presignedUrl;

        } catch (Exception e) {
            log.error("Pre-signed URL 생성 실패 - Key: {}, Error: {}", objectKey, e.getMessage(), e);
            throw new InvalidRequestException(ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    /**
     * S3 Object 존재 여부 확인 (AWS SDK v2)
     *
     * @param objectKey S3 Object Key
     * @return 존재 여부
     */
    public boolean doesObjectExist(String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            log.warn("S3 Object가 존재하지 않음 - Key: {}", objectKey);
            return false;
        } catch (Exception e) {
            log.error("S3 Object 존재 확인 실패 - Key: {}, Error: {}", objectKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * S3 연결 테스트 (버킷 접근 확인)
     */
    public void testConnection() {
        try {
            // 버킷에 대한 헤드 요청으로 연결 확인
            s3Client.listObjectsV2(builder -> builder.bucket(bucketName).maxKeys(1));
            log.info("S3 연결 테스트 성공 - Bucket: {}", bucketName);
        } catch (Exception e) {
            log.error("S3 연결 테스트 실패 - Bucket: {}, Error: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("S3 버킷에 접근할 수 없습니다: " + e.getMessage(), e);
        }
    }
}