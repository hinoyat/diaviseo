package com.s206.health.step.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepResponse {
    private Integer stepCountId;
    private Integer userId;
    private LocalDate stepDate;
    private Integer stepCount;
    private LocalDateTime createdAt;
}
