package com.s206.health.nutrition.meal.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class MealFoodRequest {

    private Integer foodId;

    private Float quantity;

    private String foodImageUrl;
}