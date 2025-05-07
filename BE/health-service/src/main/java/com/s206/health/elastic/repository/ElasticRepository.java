package com.s206.health.elastic.repository;

import com.s206.health.elastic.entity.Food;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticRepository extends ElasticsearchRepository<Food, String> {

    // 이름으로 검색
    List<Food> findByNameContaining(String name);
}