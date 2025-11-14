package com.s206.gateway.blacklist;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.redis", name = "mode", havingValue = "reactive", matchIfMissing = true)
public class ReactiveTokenBlacklistService implements BlacklistService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.mode:N/A}")
    private String mode;

    @PostConstruct
    void initLog() {
        log.warn(">>> Using ReactiveTokenBlacklistService (spring.data.redis.mode={})", mode);
    }

    @Override
    public Mono<Boolean> isBlacklisted(String rawToken) {
        try {
            String tokenHash = DigestUtils.sha256Hex(rawToken);
            String key = "blacklist:" + tokenHash;
            return redisTemplate.hasKey(key)
                    .map(Boolean::booleanValue)
                    .onErrorResume(e -> {
                        log.error("블랙리스트 조회 오류: {}", e.getMessage(), e);
                        return Mono.just(false);
                    });
        } catch (Exception e) {
            log.error("토큰 해싱/조회 예외: {}", e.getMessage(), e);
            return Mono.just(false);
        }
    }
}
