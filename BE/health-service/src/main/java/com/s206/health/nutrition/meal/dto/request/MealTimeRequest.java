package com.s206.health.nutrition.meal.dto.request;

import com.s206.health.nutrition.meal.entity.MealType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@Getter
@ToString
@Builder
public class MealTimeRequest {
    private MealType mealType;
    private LocalTime eatingTime;
    private List<MealFoodRequest> foods;
}