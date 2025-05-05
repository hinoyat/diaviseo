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
public class ExerciseCategoryResponse {
    private Integer exerciseCategoryId;
    private String exerciseCategoryName;
    private LocalDateTime createdAt;
}
