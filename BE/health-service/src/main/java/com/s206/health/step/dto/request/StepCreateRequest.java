package com.s206.health.step.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepCreateRequest {
    private LocalDate stepDate;
    private Integer stepCount;
}
