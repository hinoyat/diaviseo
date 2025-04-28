package com.s206.auth.jwt.refreshtoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "refresh:"; // 키 구분용 prefix

    // 리프레시 토큰 저장
    public void save(String refreshToken, Integer userId, String name, long expirationMillis) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("userId", userId.toString());
        valueMap.put("name", name);

        String key = generateKey(userId, refreshToken);

        redisTemplate.opsForHash().putAll(key, valueMap);
        redisTemplate.expire(key, expirationMillis, TimeUnit.MILLISECONDS);

        log.info("[Redis] Refresh Token 저장 완료 key={}", key);
    }

    // 리프레시 토큰 조회
    public Map<Object, Object> findByRefreshToken(String refreshToken) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*:" + refreshToken);
        if (keys == null || keys.isEmpty()) {
            log.warn("[Redis] 조회 실패: 해당 refreshToken 없음 token={}", refreshToken);
            return null;
        }
        String key = keys.iterator().next(); // 첫 번째 매칭 키 사용
        return redisTemplate.opsForHash().entries(key);
    }

    // 리프레시 토큰 삭제
    public void delete(String refreshToken) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*:" + refreshToken);
        if (keys == null || keys.isEmpty()) {
            log.warn("[Redis] 삭제 실패: 해당 refreshToken 없음 token={}", refreshToken);
            return;
        }
        for (String key : keys) {
            redisTemplate.delete(key);
            log.info("[Redis] Refresh Token 삭제 완료 key={}", key);
        }
    }

    // 리프레시 토큰 삭제
    public void deleteAllByUserId(Integer userId) {
        Set<String> keys = redisTemplate.keys(PREFIX + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                redisTemplate.delete(key);
                log.info("[Redis] User 전체 Refresh Token 삭제 완료 key={}", key);
            }
        } else {
            log.warn("[Redis] 삭제할 Refresh Token 없음 userId={}", userId);
        }
    }

    // 키 생성
    private String generateKey(Integer userId, String refreshToken) {
        return PREFIX + userId + ":" + refreshToken;
    }
}
