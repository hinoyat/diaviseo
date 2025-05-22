package com.s206.auth.jwt.blacklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:"; // 블랙리스트 키 프리픽스

    // Access Token을 블랙리스트에 추가
    public void addToBlacklist(String token, Date expiration) {
        try {
            long ttl = expiration.getTime() - System.currentTimeMillis();
            if (ttl <= 0) {
                // 이미 만료된 토큰
                log.info("[Blacklist] 이미 만료된 토큰, 저장하지 않음");
                return;
            }

            String tokenHash = DigestUtils.sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;

            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);

            log.info("[Blacklist] 토큰 블랙리스트 등록 완료 key={} ttl={}ms", key, ttl);

        } catch (Exception e) {
            log.error("[Blacklist] 블랙리스트 등록 실패: {}", e.getMessage(), e);
        }
    }

    // Access Token이 블랙리스트에 있는지  -> 추후 Gateway에서 처리할 메서드
    public boolean isBlacklisted(String token) {
        try {
            String tokenHash = DigestUtils.sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;
            boolean result = Boolean.TRUE.equals(redisTemplate.hasKey(key));

            if (result) {
                log.debug("[Blacklist] 블랙리스트에 존재하는 토큰 key={}", key);
            }
            return result;
        } catch (Exception e) {
            log.error("[Blacklist] 블랙리스트 조회 실패: {}", e.getMessage(), e);
            return false;
        }
    }
}
