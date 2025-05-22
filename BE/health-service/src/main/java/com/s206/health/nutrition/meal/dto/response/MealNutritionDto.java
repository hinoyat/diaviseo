package com.s206.health.nutrition.meal.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MealNutritionDto {
    private Integer totalCalorie;
    private BigDecimal totalCarbohydrate;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalSugar;
    private BigDecimal totalSodium;
}