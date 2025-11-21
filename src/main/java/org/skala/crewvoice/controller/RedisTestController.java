package org.skala.crewvoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skala.crewvoice.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "Redis Test", description = "Redis 연결 테스트 API")
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisService redisService;

    @Operation(summary = "Redis 연결 테스트")
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRedis() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 테스트 데이터 저장 (5분 TTL)
            String testKey = "test:connection";
            String testValue = "Hello Redis from Spring Boot!";
            redisService.set(testKey, testValue, 5, TimeUnit.MINUTES);

            // 데이터 조회
            Object retrieved = redisService.get(testKey);

            response.put("status", "success");
            response.put("message", "Redis 연결 성공!");
            response.put("savedValue", testValue);
            response.put("retrievedValue", retrieved);
            response.put("matched", testValue.equals(retrieved));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Redis 연결 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "값 저장")
    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> setValue(
            @RequestParam String key,
            @RequestParam String value) {

        redisService.set(key, value, 10, TimeUnit.MINUTES);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "값이 저장되었습니다");
        response.put("key", key);
        response.put("value", value);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "값 조회")
    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, Object>> getValue(@PathVariable String key) {
        Object value = redisService.get(key);

        Map<String, Object> response = new HashMap<>();
        if (value != null) {
            response.put("status", "success");
            response.put("key", key);
            response.put("value", value);
        } else {
            response.put("status", "not_found");
            response.put("message", "키를 찾을 수 없습니다");
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "값 삭제")
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, String>> deleteValue(@PathVariable String key) {
        boolean deleted = redisService.delete(key);

        Map<String, String> response = new HashMap<>();
        response.put("status", deleted ? "success" : "not_found");
        response.put("message", deleted ? "값이 삭제되었습니다" : "키를 찾을 수 없습니다");

        return ResponseEntity.ok(response);
    }
}