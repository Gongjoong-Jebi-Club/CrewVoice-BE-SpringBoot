package org.skala.crewvoice.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * 파일 업로드
     */
    public String uploadFile(MultipartFile file) {
        String fileName = generateFileName(file.getOriginalFilename());
        String key = "audio/" + fileName;

        try {
            s3Template.upload(bucketName, key, file.getInputStream());
            log.info("File uploaded to S3: bucket={}, key={}", bucketName, key);
            return key;
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 URL 생성
     */
    public String getFileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, key);
    }

    /**
     * 파일 다운로드
     */
    public InputStream downloadFile(String key) {
        try {
            return s3Template.download(bucketName, key).getInputStream();
        } catch (Exception e) {
            log.error("Failed to download file from S3: key={}", key, e);
            throw new RuntimeException("S3 download failed: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String key) {
        try {
            s3Template.deleteObject(bucketName, key);
            log.info("File deleted from S3: key={}", key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: key={}", key, e);
            throw new RuntimeException("S3 delete failed: " + e.getMessage(), e);
        }
    }

    /**
     * S3 연결 테스트
     */
    public boolean testConnection() {
        try {
            // 버킷 존재 여부 확인
            s3Template.listObjects(bucketName, "");
            log.info("S3 connection test successful: bucket={}", bucketName);
            return true;
        } catch (Exception e) {
            log.error("S3 connection test failed: bucket={}", bucketName, e);
            throw new RuntimeException("S3 connection test failed: " + e.getMessage(), e);
        }
    }

    /**
     * 고유 파일명 생성
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}