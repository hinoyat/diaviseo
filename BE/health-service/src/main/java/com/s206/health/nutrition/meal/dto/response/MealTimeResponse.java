package com.s206.health.nutrition.meal.dto.response;

import com.s206.health.nutrition.meal.entity.MealTime;
import com.s206.health.nutrition.meal.entity.MealType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MealTimeResponse {

    private Integer mealTimeId;

    private MealType mealType;

    private LocalTime eatingTime;

    private List<MealFoodResponse> foods;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static MealTimeResponse toDto(MealTime mealTime) {
        return MealTimeResponse.builder()
                .mealTimeId(mealTime.getMealTimeId())
                .mealType(mealTime.getMealType())
                .eatingTime(mealTime.getEatingTime())
                .foods(mealTime.getMealFoods().stream()
                        .map(MealFoodResponse::toDto)
                        .collect(Collectors.toList()))
                .createdAt(mealTime.getCreatedAt())
                .updatedAt(mealTime.getUpdatedAt())
                .build();
    }
}