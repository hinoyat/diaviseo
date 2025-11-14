package com.s206.gateway.controller;

import com.s206.gateway.blacklist.BlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class PerformanceTestController {

    private final BlacklistService blacklistService;

    public PerformanceTestController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
        log.warn(">>> PerformanceTestController wired with {}", blacklistService.getClass().getName());
    }

    @GetMapping("/blacklist-check")
    public Mono<Map<String, Object>> checkBlacklist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        long start = System.currentTimeMillis();
        String token = authHeader.replace("Bearer ", "");

        return blacklistService.isBlacklisted(token)
                .map(isBlacklisted -> {
                    long duration = System.currentTimeMillis() - start;
                    return Map.of(
                            "isBlacklisted", isBlacklisted,
                            "duration", duration,
                            "timestamp", System.currentTimeMillis()
                    );
                });
    }
}