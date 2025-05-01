package com.s206.gateway.util;

import com.s206.gateway.blacklist.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final TokenBlacklistService tokenBlacklistService;

    // 서명 키 초기화
    @PostConstruct
    public void init() {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // 토큰 유효성 검증
    public Mono<Boolean> validateToken(String token) {
        try {
            // 1. 서명 및 만료 검증 (자동)
            Claims claims = parseClaims(token);

            // 2. tokenType 검증
            String tokenType = (String) claims.get("tokenType");
            if (tokenType == null || !"access".equals(tokenType)) {
                // access 토큰이 아닌 경우 (예: refresh) → 유효하지 않음
                return Mono.just(false);
            }

            // 3. 블랙리스트 확인
            return tokenBlacklistService.isBlacklisted(token)
                    .map(isBlacklisted -> {
                        if (isBlacklisted) {
                            log.debug("블랙리스트 토큰으로 확인됨");
                        }
                        return !isBlacklisted; // 블랙리스트에 없으면 true
                    });

        } catch (JwtException | IllegalArgumentException e) {
            // 1-1. 서명 불일치, 만료, 잘못된 토큰 구조 등 예외 발생
            log.warn("유효하지 않은 JWT: {}", e.getMessage());
            return Mono.just(false);
        }
    }

    // JWT에서 userId 클레임 추출
    public int getUserId(String token) {
        Claims claims = parseClaims(token);
        return (Integer) claims.get("userId");
    }

    // JWT Claims 파싱 (서명 + 만료 확인 포함)
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 유효성 검증 + 파싱 수행
                .getBody(); // 클레임 추출
    }
}
