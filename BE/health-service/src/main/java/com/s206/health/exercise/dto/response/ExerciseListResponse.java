package com.s206.health.exercise.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseListResponse {
    private Long exerciseId;
    private Long userId;
    private Long exerciseTypeId;
    private String exerciseName; // ExerciseType 에서 조회
    private String exerciseCategoryName; // ExerciseCategory 에서 조회
    private LocalDateTime exerciseDate;
    private Integer exerciseTime;
    private Integer exerciseCalorie;
    private LocalDateTime createdAt;
}
