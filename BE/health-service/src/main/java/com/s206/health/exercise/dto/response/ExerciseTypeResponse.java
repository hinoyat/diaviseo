package com.s206.health.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseTypeResponse {
    private Integer exerciseTypeId;
    private Integer exerciseCategoryId;
    private String exerciseName;
    private String exerciseEnglishName;
    private Integer exerciseCalorie;
    private Integer exerciseNumber;
    private LocalDateTime createdAt;
}