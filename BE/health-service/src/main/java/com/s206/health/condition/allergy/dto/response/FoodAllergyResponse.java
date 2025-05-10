package com.s206.health.condition.allergy.dto.response;

import com.s206.health.condition.allergy.entity.FoodAllergy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodAllergyResponse {

    private Long allergyId;
    private String allergyName;

    public static FoodAllergyResponse toDto(FoodAllergy allergy) {
        return FoodAllergyResponse.builder()
                .allergyId(allergy.getId())
                .allergyName(allergy.getName())
                .build();
    }
}
