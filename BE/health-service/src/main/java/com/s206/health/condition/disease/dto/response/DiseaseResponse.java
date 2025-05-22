package com.s206.health.condition.disease.dto.response;

import com.s206.health.condition.disease.entity.Disease;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiseaseResponse {

    private Long diseaseId;
    private String diseaseName;
    private String category;

    public static DiseaseResponse toDto(Disease disease) {
        return DiseaseResponse.builder()
                .diseaseId(disease.getId())
                .diseaseName(disease.getName())
                .category(disease.getCategory())
                .build();
    }
}
