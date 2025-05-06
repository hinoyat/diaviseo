package com.s206.health.exercise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TodayExerciseStatsResponse {
    private LocalDateTime date;
    private Integer totalCalories;
    private Integer totalExerciseTime;
    private Integer exerciseCount;
    private List<ExerciseDetail> exercises;

    @Getter
    @Builder
    public static class ExerciseDetail {
        private Integer exerciseId;
        private String exerciseName;
        private String categoryName;
        private LocalDateTime exerciseDate;
        private Integer exerciseTime;
        private Integer exerciseCalorie;
    }
}
