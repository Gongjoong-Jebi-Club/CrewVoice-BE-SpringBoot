package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationCacheService {

    private final RedisService redisService;

    private static final String EVALUATION_PREFIX = "evaluation:";
    private static final long CACHE_TTL = 30; // 30분

    /**
     * 평가 결과 캐싱
     */
    public void cacheEvaluationResult(String evaluationId, Map<String, Object> result) {
        String key = EVALUATION_PREFIX + evaluationId;
        redisService.set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        log.info("평가 결과 캐싱: evaluationId={}", evaluationId);
    }

    /**
     * 캐시된 평가 결과 조회
     */
    public Map<String, Object> getCachedEvaluationResult(String evaluationId) {
        String key = EVALUATION_PREFIX + evaluationId;
        Object cached = redisService.get(key);

        if (cached != null) {
            log.info("캐시 HIT: evaluationId={}", evaluationId);
            return (Map<String, Object>) cached;
        }

        log.info("캐시 MISS: evaluationId={}", evaluationId);
        return null;
    }

    /**
     * 평가 진행 상태 저장 (Hash)
     */
    public void setEvaluationStatus(String evaluationId, String status) {
        String key = "evaluation:status:" + evaluationId;
        redisService.hSet(key, "status", status);
        redisService.hSet(key, "updatedAt", System.currentTimeMillis());
        redisService.expire(key, 1, TimeUnit.HOURS);
    }

    /**
     * 평가 진행 상태 조회
     */
    public String getEvaluationStatus(String evaluationId) {
        String key = "evaluation:status:" + evaluationId;
        Object status = redisService.hGet(key, "status");
        return status != null ? status.toString() : null;
    }
}