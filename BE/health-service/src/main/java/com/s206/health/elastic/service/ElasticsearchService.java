package com.s206.health.elastic.service;

import com.s206.health.elastic.document.ElasticFood;
import com.s206.health.elastic.repository.ElasticRepository;
import com.s206.health.nutrition.favorite.repository.FavoriteFoodRepository;
import com.s206.health.nutrition.food.dto.response.FoodListResponse;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticRepository elasticRepository; // Elasticsearch 레포지토리
    private final FoodRepository foodRepository; // MySQL 음식 레포지토리
    private final FavoriteFoodRepository favoriteFoodRepository;

    // 음식 저장
    public ElasticFood save(ElasticFood food) {
        return elasticRepository.save(food);
    }

    // 이름으로 음식 검색
    public List<FoodListResponse> searchByName(String name, Integer userId) {
        List<ElasticFood> elasticFoods = elasticRepository.findByNameContaining(name);
        List<FoodListResponse> result = new ArrayList<>();

        for (ElasticFood elasticFood : elasticFoods) {
            Optional<Food> foodOpt = foodRepository.findById(elasticFood.getFoodId());

            if (foodOpt.isPresent()) {
                Food food = foodOpt.get();
                // 실제 즐겨찾기 정보 조회
                boolean isFavorite = favoriteFoodRepository.existsByUserIdAndFoodFoodId(userId, food.getFoodId());
                result.add(FoodListResponse.toDto(food, isFavorite));
            }
        }

        return result;
    }

    // 모든 음식 조회
    public List<ElasticFood> findAll() {
        Iterable<ElasticFood> foods = elasticRepository.findAll();
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
            Boolean isDeleted = mysqlFood.getIsDeleted();
            if (Boolean.TRUE.equals(isDeleted)) {
                continue; // 삭제된 데이터는 건너뜀
            }

            // Elasticsearch Food 엔티티로 변환
            ElasticFood esFood = ElasticFood.builder()
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