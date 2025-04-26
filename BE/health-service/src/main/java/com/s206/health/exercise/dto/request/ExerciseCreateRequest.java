package com.s206.health.exercise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCreateRequest {
    private Integer userId;
    private Integer exerciseTypeId;
    private LocalDateTime exerciseDate;
    private Integer exerciseTime;
}
