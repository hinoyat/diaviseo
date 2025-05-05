package com.s206.health.nutrition.meal.dto.response;

import com.s206.health.nutrition.meal.entity.MealFood;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealFoodResponse {

    private Integer mealFoodId;

    private Integer foodId;

    private String foodName;

    private Integer calorie;

    private Integer quantity;

    private String foodImageUrl;

    public static MealFoodResponse toDto(MealFood mealFood) {
        return MealFoodResponse.builder()
                .mealFoodId(mealFood.getMealFoodId())
                .foodId(mealFood.getFood().getFoodId())
                .foodName(mealFood.getFood().getFoodName())
                .calorie(mealFood.getFood().getCalorie())
                .quantity(mealFood.getQuantity())
                .foodImageUrl(mealFood.getFoodImageUrl())
                .build();
    }
}