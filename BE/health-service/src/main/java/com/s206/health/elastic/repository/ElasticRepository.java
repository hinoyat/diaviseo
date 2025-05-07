package com.s206.health.elastic.repository;

import com.s206.health.elastic.document.ElasticFood;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticRepository extends ElasticsearchRepository<ElasticFood, String> {

    // 이름으로 검색
    List<ElasticFood> findByNameContaining(String name);
}