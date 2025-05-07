package com.s206.health.elastic.config;

import com.s206.health.elastic.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// 애플리케이션 시작 시 MySQL의 음식 데이터를 Elasticsearch로 동기화
@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ElasticsearchService elasticsearchService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("애플리케이션 시작 완료: MySQL → Elasticsearch 데이터 동기화 시작");
        try {
            elasticsearchService.syncFoodsFromDatabase(); // MySQL -> Elasticsearch 동기화 실행
            log.info("Elasticsearch 데이터 동기화 완료");
        } catch (Exception e) {
            log.error("Elasticsearch 데이터 동기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}