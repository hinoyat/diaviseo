package com.s206.health.nutrition.statistics.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NutritionStatsEntry {
    private String label;
    private Integer calorie;
    private Integer carbs;
    private Integer protein;
    private Integer fat;
    private Integer sugar;

    public Integer getTotal() {
        return carbs + protein + fat + sugar;
    }
}