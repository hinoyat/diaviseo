package com.s206.health.elastic.service;

import com.s206.health.elastic.document.ElasticFood;
import com.s206.health.elastic.repository.ElasticRepository;
import com.s206.health.nutrition.favorite.repository.FavoriteFoodRepository;
import com.s206.health.nutrition.food.dto.response.FoodDetailResponse;
import com.s206.health.nutrition.food.dto.response.FoodListResponse;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticRepository elasticRepository; // Elasticsearch 레포지토리
    private final FoodRepository foodRepository; // MySQL 음식 레포지토리
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // 음식 저장
    public ElasticFood save(ElasticFood food) {
        return elasticRepository.save(food);
    }

    // 이름으로 음식 검색
    public List<FoodDetailResponse> searchByName(String name, Integer userId) {
        // CriteriaQuery 사용 (직관적인 방식으로 쿼리 작성)
        Criteria criteria = new Criteria("name")
                .contains(name) // 부분 일치 확인
                .or("name").matches(name); // 전체 일치 확인

        Query searchQuery = new CriteriaQuery(criteria);

        // elasticsearchOperations를 사용하여 쿼리 실행
        SearchHits<ElasticFood> searchHits = elasticsearchOperations.search(searchQuery, ElasticFood.class);

        // 검색된 모든 foodId 추출
        List<Integer> foodIds = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getFoodId())
                .collect(Collectors.toList());

        if (foodIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 모든 foodId를 한 번에 조회 (IN 쿼리 사용)
        List<Food> foods = foodRepository.findAllByFoodIdIn(foodIds);

        // 즐겨찾기 정보 한 번에 조회 (IN 쿼리 사용)
        Set<Integer> favoriteFoodIds = new HashSet<>();
        if (userId != null) {
            favoriteFoodIds = favoriteFoodRepository.findAllByUserIdAndFoodFoodIdIn(userId, foodIds)
                    .stream()
                    .map(favoriteFood -> favoriteFood.getFood().getFoodId())
                    .collect(Collectors.toSet());
        }

        // 결과 변환
        List<FoodDetailResponse> result = new ArrayList<>();
        for (Food food : foods) {
            boolean isFavorite = favoriteFoodIds.contains(food.getFoodId());
            result.add(FoodDetailResponse.toDto(food, isFavorite));
        }

        // 검색 결과 순서 유지 (Elasticsearch 결과 순서와 일치시키기)
        Map<Integer, FoodDetailResponse> responseMap = result.stream()
                .collect(Collectors.toMap(FoodDetailResponse::getFoodId, Function.identity()));

        return foodIds.stream()
                .map(id -> responseMap.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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