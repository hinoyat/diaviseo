package com.s206.health.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExerciseStatsResponse {
    private List<MonthlyExercise> monthlyExercises;

    @Getter
    @Builder
    public static class MonthlyExercise {
        private YearMonth yearMonth;
        private Double avgDailyCalories;
        private Integer totalExerciseCount;
        private Integer totalCalories;
    }
}
