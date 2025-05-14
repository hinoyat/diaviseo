package com.s206.health.exercise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseUpdateRequest {
    private LocalDateTime exerciseDate;
    private Integer exerciseTime;
    private Integer exerciseCalorie;
}