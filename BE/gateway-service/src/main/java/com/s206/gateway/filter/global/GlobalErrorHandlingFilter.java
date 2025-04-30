package com.s206.gateway.filter.global;

import com.s206.gateway.util.GatewayResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorHandlingFilter implements GlobalFilter, Ordered {

    private final GatewayResponseUtil gatewayResponseUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(throwable -> {
                    log.error("[Gateway 예외 처리] 예외 발생: {}", throwable.getMessage(), throwable);

                    // 커밋된 응답이면 무시 (이미 응답 보낸 경우)
                    if (exchange.getResponse().isCommitted()) {
                        return Mono.error(throwable);
                    }

                    // 예외 내용에 따라 커스터마이징 가능
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    String message = "Gateway 내부 오류가 발생했습니다.";

                    return gatewayResponseUtil.writeJsonError(exchange, status, message);
                });
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE; // 맨 마지막에 실행되도록 설정
    }
}