package com.s206.health.nutrition.meal.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WeeklyNutritionResponse {

    private LocalDate startDate;

    private LocalDate endDate;

    private List<DailyNutritionResponse> dailyNutritions;
}