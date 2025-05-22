package com.s206.health.nutrition.foodset.dto.response;

import com.s206.health.nutrition.foodset.entity.FoodSet;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodSetListResponse {

    private Integer foodSetId;

    private String name;

    private Integer foodCount;

    private Integer totalCalories;


    public static FoodSetListResponse toDto(FoodSet foodSet) {
        int foodCount = foodSet.getFoodSetFoods().size();
        int totalCalories = foodSet.getFoodSetFoods().stream()
                .mapToInt(f -> (int) (f.getFood().getCalorie() * f.getQuantity()))
                .sum();

        return FoodSetListResponse.builder()
                .foodSetId(foodSet.getFoodSetId())
                .name(foodSet.getName())
                .foodCount(foodCount)
                .totalCalories(totalCalories)
                .build();
    }

}
