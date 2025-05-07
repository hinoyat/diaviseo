package com.s206.health.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayExerciseStatsResponse {
    private LocalDateTime date;
    private Integer totalCalories;
    private Integer totalExerciseTime;
    private Integer exerciseCount;
    private List<ExerciseDetail> exercises;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseDetail {
        private Integer exerciseId;
        private String exerciseName;
        private String categoryName;
        private LocalDateTime exerciseDate;
        private Integer exerciseTime;
        private Integer exerciseCalorie;
    }
}
