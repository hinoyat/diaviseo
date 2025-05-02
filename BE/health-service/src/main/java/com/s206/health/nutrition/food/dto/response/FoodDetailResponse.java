package com.s206.health.nutrition.food.dto.response;

import com.s206.health.nutrition.food.entity.Food;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class FoodDetailResponse {
    private Integer foodId;
    private String foodName;
    private Integer calorie;
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal sweet;
    private BigDecimal sodium;
    private BigDecimal saturatedFat;
    private BigDecimal transFat;
    private BigDecimal cholesterol;
    private boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FoodDetailResponse toDto(Food food, boolean isFavorite) {
        return FoodDetailResponse.builder()
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .calorie(food.getCalorie())
                .carbohydrate(food.getCarbohydrate())
                .protein(food.getProtein())
                .fat(food.getFat())
                .sweet(food.getSweet())
                .sodium(food.getSodium())
                .saturatedFat(food.getSaturatedFat())
                .transFat(food.getTransFat())
                .cholesterol(food.getCholesterol())
                .isFavorite(isFavorite)
                .createdAt(food.getCreatedAt())
                .updatedAt(food.getUpdatedAt())
                .build();
    }
}
