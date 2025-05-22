package com.s206.health.elastic.repository;

import com.s206.health.elastic.document.ElasticFood;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticRepository extends ElasticsearchRepository<ElasticFood, String> {

    // 다중 조건 검색: 유연한 검색을 위해 여러 검색 방식 조합
    @Query("""
        {
        "bool": {
            "should": [
                {
                    "wildcard": {
                        "name": "*?0*"
                    }
                },
                {
                    "match_phrase_prefix": {
                        "name": "?0"
                    }
                }
            ]
        }
    }""")
    List<ElasticFood> findByNameFlexible(String name);
}