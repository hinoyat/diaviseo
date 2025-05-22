package com.s206.health.nutrition.meal.dto.response;

import com.s206.health.nutrition.meal.entity.Meal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MealDetailResponse {

    private Integer mealId;

    private LocalDate mealDate;

    private Boolean isMeal;

    private List<MealTimeResponse> mealTimes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static MealDetailResponse toDto(Meal meal) {
        return MealDetailResponse.builder()
                .mealId(meal.getMealId())
                .mealDate(meal.getMealDate())
                .isMeal(meal.getIsMeal())
                .mealTimes(meal.getMealTimes().stream()
                        .filter(mealTime -> !mealTime.getIsDeleted())
                        .map(MealTimeResponse::toDto)
                        .collect(Collectors.toList()))
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }
}