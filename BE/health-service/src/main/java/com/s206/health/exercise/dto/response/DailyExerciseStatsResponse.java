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
public class DailyExerciseStatsResponse {
    private List<DailyExercise> dailyExercises;

    @Getter
    @Builder
    public static class DailyExercise {
        private LocalDate date;
        private Integer totalCalories;
        private Integer exerciseCount;
        private List<ExerciseDetail> exercises;
    }

    @Getter
    @Builder
    public static class ExerciseDetail {
        private Integer exerciseId;
        private String exerciseName;
        private String categoryName;
        private Integer exerciseTime;
        private Integer exerciseCalorie;
    }
}
