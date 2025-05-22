package com.s206.health.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyExerciseStatsResponse {
    private List<WeeklyExercise> weeklyExercises;

    @Getter
    @Builder
    public static class WeeklyExercise {
        private LocalDate startDate;
        private LocalDate endDate;
        private Double avgDailyCalories;
        private Integer totalExerciseCount;
        private Integer totalCalories;
    }
}
