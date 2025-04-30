package com.s206.gateway.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class GatewayResponseUtil {

    public Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = String.format("""
            {
              "timestamp": "%s",
              "status": %d,
              "message": "%s",
              "data": {
                "gateway": true
              }
            }
            """, LocalDateTime.now(), status.value(), message);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(responseJson.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
