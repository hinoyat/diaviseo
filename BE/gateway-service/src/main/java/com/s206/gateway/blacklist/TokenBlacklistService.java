package com.s206.gateway.blacklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    // 주어진 토큰이 블랙리스트에 존재하는지 확인
    public Mono<Boolean> isBlacklisted(String token) {
        try {
            // 토큰을 SHA-256으로 해싱하여 Redis 키 생성
            String tokenHash = DigestUtils.sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;

            // Redis에서 해당 키 존재 여부 확인
            return redisTemplate.hasKey(key)
                    .doOnNext(isBlacklisted -> {
                        if (isBlacklisted) {
                            log.debug("블랙리스트에 존재하는 토큰입니다. key prefix: {}", tokenHash.substring(0, 10));
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("블랙리스트 조회 중 오류 발생: {}", e.getMessage(), e);
                        return Mono.just(false); // 오류 발생 시 기본값 false
                    });

        } catch (Exception e) {
            log.error("토큰 해싱 실패 또는 블랙리스트 조회 중 예외 발생: {}", e.getMessage(), e);
            return Mono.just(false);
        }
    }
}
