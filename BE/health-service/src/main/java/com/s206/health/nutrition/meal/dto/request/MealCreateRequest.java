package com.s206.health.nutrition.meal.dto.request;

import com.s206.health.nutrition.meal.entity.MealType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@Builder
public class MealCreateRequest {

    private LocalDate mealDate;

    private Boolean isMeal;

    private List<MealTimeRequest> mealTimes;
}