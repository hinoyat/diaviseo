package com.s206.gateway.blacklist;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.redis", name = "mode", havingValue = "sync")
public class SyncTokenBlacklistService implements BlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.mode:N/A}")
    private String mode;

    @PostConstruct
    void initLog() {
        log.warn(">>> Using SyncTokenBlacklistService (spring.data.redis.mode={})", mode);
    }

    @Override
    public Mono<Boolean> isBlacklisted(String rawToken) {
        return Mono.fromCallable(() -> {
            try {
                String tokenHash = DigestUtils.sha256Hex(rawToken);
                String key = "blacklist:" + tokenHash;
                Boolean has = redisTemplate.hasKey(key);
                return Boolean.TRUE.equals(has);
            } catch (Exception e) {
                log.error("블랙리스트 조회 예외(sync): {}", e.getMessage(), e);
                return false;
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
}
