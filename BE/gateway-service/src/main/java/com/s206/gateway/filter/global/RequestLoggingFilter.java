package com.s206.gateway.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        HttpHeaders headers = exchange.getRequest().getHeaders();

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String userIdHeader = headers.getFirst("X-USER-ID");

        // 로그 출력
        log.info("[요청 로그] {} {}", method, path);
        if (authorization != null) {
            String maskedToken = authorization.length() > 20 ? authorization.substring(0, 20) + "..." : authorization;
            log.info("[요청 헤더] Authorization: {}", maskedToken);
        }
        if (userIdHeader != null) {
            log.info("[요청 헤더] X-USER-ID: {}", userIdHeader);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1; // 나중에 실행
    }
}