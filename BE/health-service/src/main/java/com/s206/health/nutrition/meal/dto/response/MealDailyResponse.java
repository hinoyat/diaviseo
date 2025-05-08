package com.s206.health.nutrition.meal.dto.response;

import lombok.Builder;
import lombok.Getter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MealDailyResponse {
    private Integer mealId;
    private LocalDate mealDate;
    private Boolean isMeal;
    private MealNutritionDto totalNutrition;
    private List<MealTimeNutritionResponse> mealTimes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
