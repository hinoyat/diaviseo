package com.s206.health.nutrition.meal.dto.response;

import com.s206.health.nutrition.meal.entity.MealType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class MealTimeNutritionResponse {
    private Integer mealTimeId;
    private MealType mealType;
    private LocalTime eatingTime;
    private List<MealFoodResponse> foods;
    private MealNutritionDto nutrition;
    private String mealTimeImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
