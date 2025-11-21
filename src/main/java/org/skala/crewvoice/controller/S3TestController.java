package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.service.S3Service;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "S3 Test", description = "S3 업로드/다운로드 테스트 API")
@Slf4j
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3TestController {

    private final S3Service s3Service;

    @Operation(summary = "S3 연결 테스트", description = "S3 설정이 올바른지 확인합니다")
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {

        Map<String, Object> response = new HashMap<>();

        try {
            // 실제 S3 연결 테스트
            s3Service.testConnection();

            response.put("status", "success");
            response.put("message", "S3 연결 성공! 버킷에 정상적으로 접근할 수 있습니다.");
            response.put("timestamp", System.currentTimeMillis());

            log.info("S3 connection test: SUCCESS");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("S3 connection test failed", e);
            response.put("status", "error");
            response.put("message", "S3 연결 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}