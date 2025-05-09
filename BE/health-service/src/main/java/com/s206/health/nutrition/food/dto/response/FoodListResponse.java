package com.s206.health.nutrition.food.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.health.nutrition.food.entity.Food;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class FoodListResponse {
    private Integer foodId;
    private String foodName;
    private Integer calorie;
    private BigDecimal baseAmount;
    private boolean isFavorite;

    @JsonProperty("isFavorite")
    public boolean getIsFavorite() {
        return isFavorite;
    }

    public static FoodListResponse toDto(Food food, boolean isFavorite) {
        return FoodListResponse.builder()
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .calorie(food.getCalorie())
                .isFavorite(isFavorite)
                .baseAmount(food.getBaseAmount())
                .build();
    }
}