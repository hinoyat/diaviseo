package com.s206.health.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("ocrTaskExecutor")
    public ThreadPoolTaskExecutor ocrTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);            // 기본 스레드 수
        executor.setMaxPoolSize(4);             // 최대 스레드 수
        executor.setQueueCapacity(10);          // 큐 용량
        executor.setThreadNamePrefix("OCR-");   // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}