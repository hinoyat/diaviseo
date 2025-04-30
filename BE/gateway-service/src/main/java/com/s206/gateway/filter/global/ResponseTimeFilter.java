package com.s206.gateway.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ResponseTimeFilter implements GlobalFilter, Ordered {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청 시작 시간 기록
        exchange.getAttributes().put(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    Long startTime = exchange.getAttribute(START_TIME_ATTRIBUTE);
                    if (startTime != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        String path = exchange.getRequest().getURI().getPath();
                        log.info("[처리 시간] {} → {} ms", path, duration);
                    }
                });
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE; // 가장 마지막에 실행되도록
    }
}