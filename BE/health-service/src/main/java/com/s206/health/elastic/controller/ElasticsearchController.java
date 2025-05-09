package com.s206.health.elastic.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.elastic.document.ElasticFood;
import com.s206.health.elastic.service.ElasticsearchService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ResponseDto<List<ElasticFood>>> searchByName(@RequestParam String name) {
        List<ElasticFood> foods = elasticsearchService.searchByName(name);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"검색 성공",foods));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<ElasticFood>> createFood(@RequestBody ElasticFood food) {
        ElasticFood savedFood = elasticsearchService.save(food);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"저장 성공",savedFood));
    }

    // 모든 음식 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<ElasticFood>>> findAllFoods() {
        List<ElasticFood> foods = elasticsearchService.findAll();
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"모든 검색 성공",foods));
    }

    // MySQL 데이터를 Elasticsearch 로 수동 동기화
    @PostMapping("/sync")
    public ResponseEntity<ResponseDto<String>> syncFoods() {
        elasticsearchService.syncFoodsFromDatabase();
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"동기화 성공"));
    }
}