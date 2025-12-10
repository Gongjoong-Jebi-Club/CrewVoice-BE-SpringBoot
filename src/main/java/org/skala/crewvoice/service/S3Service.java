package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
     * S3에 파일 업로드 (STEP2용)
     *
     * @param file 업로드할 파일
     * @param evaluationId 평가 ID
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @param language 언어 코드 ("ko-KR" 또는 "en-US")
     * @return S3 Object Key
     */
    public String uploadFileForStep2(MultipartFile file, Long evaluationId, Long userId, Long questionId, String language) {
        try {
            // 언어 코드를 파일명용으로 변환 (ko-KR -> korean, en-US -> english)
            String languageName = language.startsWith("ko") ? "korean" : "english";

            // Object Key 생성: audios/step2/eval_{평가ID}_{유저ID}_{문제ID}_{언어}.wav
            String objectKey = String.format("audios/step2/eval_%d_%d_%d_%s.wav",
                    evaluationId, userId, questionId, languageName);

            log.info("S3 파일 업로드 시작 - Bucket: {}, Key: {}, Size: {} bytes",
                    bucketName, objectKey, file.getSize());

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            log.info("S3 파일 업로드 완료 - Object Key: {}", objectKey);
            return objectKey;

        } catch (IOException e) {
            log.error("파일 읽기 실패 - Error: {}", e.getMessage(), e);
            throw new InvalidRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패 - Error: {}", e.getMessage(), e);
            throw new InvalidRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * S3에 파일 업로드 (STEP3용)
     *
     * @param file 업로드할 파일
     * @param evaluationId 평가 ID
     * @param userId 사용자 ID
     * @param questionNumber 질문 번호 (1, 2, 3)
     * @param language 언어 코드 ("ko-KR" 또는 "en-US")
     * @return S3 Object Key
     */
    public String uploadFileForStep3(MultipartFile file, Long evaluationId, Long userId,
                                     Integer questionNumber, String language) {
        try {
            // 언어 코드를 파일명용으로 변환
            String languageName = language.startsWith("ko") ? "korean" : "english";

            // Object Key 생성: evaluations/{평가ID}/step3/user{유저ID}_q{질문번호}_{언어}.wav
            String objectKey = String.format("evaluations/%d/step3/user%d_q%d_%s.wav",
                    evaluationId, userId, questionNumber, languageName);

            log.info("S3 파일 업로드 시작 - Bucket: {}, Key: {}, Size: {} bytes",
                    bucketName, objectKey, file.getSize());

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            log.info("S3 파일 업로드 완료 - Object Key: {}", objectKey);
            return objectKey;

        } catch (IOException e) {
            log.error("파일 읽기 실패 - Error: {}", e.getMessage(), e);
            throw new InvalidRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패 - Error: {}", e.getMessage(), e);
            throw new InvalidRequestException(ErrorCode.FILE_UPLOAD_FAILED);
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