package com.s206.health.exercise.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCreateRequest {
    private Integer exerciseNumber;
    private LocalDateTime exerciseDate;
    private Integer exerciseTime;
    private Integer exerciseCalorie;
    private String healthConnectUuid;
}
