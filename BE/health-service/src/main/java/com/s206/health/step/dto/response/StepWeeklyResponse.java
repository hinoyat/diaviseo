package com.s206.health.step.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepWeeklyResponse {
    private List<StepDailyData> weeklySteps;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalSteps;
    private Integer avgSteps;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepDailyData {
        private LocalDate date;
        private Integer stepCount;
    }
}
