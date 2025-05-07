package com.s206.health.elastic.service;

import com.s206.health.elastic.entity.Food;
import com.s206.health.elastic.repository.ElasticRepository;
import com.s206.health.nutrition.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticRepository elasticRepository; // Elasticsearch 레포지토리
    private final FoodRepository foodRepository; // MySQL 음식 레포지토리

    // 음식 저장
    public Food save(Food food) {
        return elasticRepository.save(food);
    }

    // 이름으로 음식 검색
    public List<Food> searchByName(String name) {
        return elasticRepository.findByNameContaining(name);
    }

    // 모든 음식 조회
    public List<Food> findAll() {
        Iterable<Food> foods = elasticRepository.findAll();
        return StreamSupport.stream(foods.spliterator(), false)
                .collect(Collectors.toList());
    }

    // MySQL 음식 데이터를 Elasticsearch 로 동기화
    public void syncFoodsFromDatabase() {
        log.info("MySQL 음식 데이터 조회 시작");
        List<com.s206.health.nutrition.food.entity.Food> mysqlFoods = foodRepository.findAll();
        log.info("총 {} 개의 음식 데이터 조회됨", mysqlFoods.size());

        int syncCount = 0;
        for (com.s206.health.nutrition.food.entity.Food mysqlFood : mysqlFoods) {
            if (mysqlFood.getIsDeleted()) {
                continue; // 삭제된 데이터는 건너뜀
            }

            // Elasticsearch Food 엔티티로 변환
            Food esFood = Food.builder()
                    .id(mysqlFood.getFoodId().toString()) // ID를 MySQL의 foodId로 설정
                    .foodId(mysqlFood.getFoodId())        // foodId 필드 설정
                    .name(mysqlFood.getFoodName())        // 음식 이름 설정
                    .build();

            // Elasticsearch 에 저장
            elasticRepository.save(esFood);
            syncCount++;
        }

        log.info("Elasticsearch 동기화 완료: {} 개의 음식 데이터 처리됨", syncCount);
    }
}