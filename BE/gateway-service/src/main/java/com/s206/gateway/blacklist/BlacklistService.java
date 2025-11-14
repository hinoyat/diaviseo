package com.s206.gateway.blacklist;

import reactor.core.publisher.Mono;

public interface BlacklistService {
    Mono<Boolean> isBlacklisted(String token);
}
