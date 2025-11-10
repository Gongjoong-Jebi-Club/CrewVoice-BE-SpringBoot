package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 값 저장
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        log.debug("Redis SET: key={}", key);
    }

    /**
     * 값 저장 (TTL 설정)
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        log.debug("Redis SET: key={}, ttl={}{}", key, timeout, unit);
    }

    /**
     * 값 조회
     */
    public Object get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        log.debug("Redis GET: key={}, found={}", key, value != null);
        return value;
    }

    /**
     * 값 삭제
     */
    public boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        log.debug("Redis DELETE: key={}, success={}", key, result);
        return result != null && result;
    }

    /**
     * 키 존재 여부 확인
     */
    public boolean exists(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    /**
     * TTL 설정
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.expire(key, timeout, unit);
        log.debug("Redis EXPIRE: key={}, ttl={}{}, success={}", key, timeout, unit, result);
        return result != null && result;
    }

    /**
     * Hash 저장
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
        log.debug("Redis HSET: key={}, field={}", key, field);
    }

    /**
     * Hash 조회
     */
    public Object hGet(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        log.debug("Redis HGET: key={}, field={}, found={}", key, field, value != null);
        return value;
    }

    /**
     * Hash 전체 조회
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Hash 삭제
     */
    public boolean hDelete(String key, String... fields) {
        Long result = redisTemplate.opsForHash().delete(key, (Object[]) fields);
        return result != null && result > 0;
    }

    /**
     * 패턴으로 키 찾기
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}