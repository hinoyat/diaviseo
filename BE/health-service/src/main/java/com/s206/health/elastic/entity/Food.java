package com.s206.health.elastic.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "food") // Elasticsearch 인덱스 이름 지정
@Setting(settingPath = "elastic-settings.json")
public class Food {

    @Id // Elasticsearch 문서 ID
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer foodId;
}