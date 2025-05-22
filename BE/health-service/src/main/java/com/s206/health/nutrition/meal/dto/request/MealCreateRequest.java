package com.s206.health.nutrition.meal.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class MealCreateRequest {

    private LocalDate mealDate;

    private Boolean isMeal;

    private List<MealTimeRequest> mealTimes;
}