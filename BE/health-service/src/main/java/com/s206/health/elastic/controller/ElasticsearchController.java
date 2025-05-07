package com.s206.health.elastic.controller;

import com.s206.health.elastic.entity.Food;
import com.s206.health.elastic.service.ElasticsearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods/search")
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    public ElasticsearchController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    // 음식 이름으로 검색
    @GetMapping("/name")
    public ResponseEntity<List<Food>> searchByName(@RequestParam String name) {
        List<Food> foods = elasticsearchService.searchByName(name);
        return ResponseEntity.ok(foods);
    }

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody Food food) {
        Food savedFood = elasticsearchService.save(food);
        return ResponseEntity.ok(savedFood);
    }

    // 모든 음식 조회
    @GetMapping
    public ResponseEntity<List<Food>> findAllFoods() {
        List<Food> foods = elasticsearchService.findAll();
        return ResponseEntity.ok(foods);
    }

    // MySQL 데이터를 Elasticsearch 로 수동 동기화
    @PostMapping("/sync")
    public ResponseEntity<String> syncFoods() {
        elasticsearchService.syncFoodsFromDatabase();
        return ResponseEntity.ok("Food data synchronized with Elasticsearch");
    }
}