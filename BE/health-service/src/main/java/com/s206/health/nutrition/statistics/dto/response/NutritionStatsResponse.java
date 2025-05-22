package com.s206.health.nutrition.statistics.dto.response;

import com.s206.health.nutrition.statistics.type.PeriodType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class NutritionStatsResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private PeriodType periodType;
    private List<NutritionStatsEntry> data;
}