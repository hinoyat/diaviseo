package com.s206.gateway.filter;

import com.s206.gateway.blacklist.TokenBlacklistService;
import com.s206.gateway.util.GatewayResponseUtil;
import com.s206.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;
    private final GatewayResponseUtil gatewayResponseUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthFilter(JwtUtil jwtUtil, GatewayResponseUtil gatewayResponseUtil, TokenBlacklistService tokenBlacklistService) {
        super(JwtAuthFilter.Config.class);
        this.jwtUtil = jwtUtil;
        this.gatewayResponseUtil = gatewayResponseUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange);

            if (token == null) {
                log.warn("[JWT 필터] Authorization 헤더 없음 또는 형식 불일치");
                return gatewayResponseUtil.writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없습니다.");
            }

            return tokenBlacklistService.isBlacklisted(token)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            log.warn("[JWT 필터] 블랙리스트 토큰 접근 차단");
                            return gatewayResponseUtil.writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "해당 토큰은 로그아웃된 상태입니다.");
                        }

                        return jwtUtil.validateToken(token)
                                .flatMap(valid -> {
                                    if (!valid) {
                                        log.warn("[JWT 필터] 유효하지 않은 토큰");
                                        return gatewayResponseUtil.writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                                    }

                                    int userId = jwtUtil.getUserId(token);
                                    ServerWebExchange modifiedExchange = exchange.mutate()
                                            .request(builder -> builder.header("X-USER-ID", String.valueOf(userId)))
                                            .build();
                                    log.info("[인증 완료] 유효한 JWT → X-USER-ID: {}", userId);
                                    return chain.filter(modifiedExchange);
                                });
                    });
        };
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }
}

