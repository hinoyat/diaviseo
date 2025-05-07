package com.s206.health.nutrition.statistics.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.statistics.dto.response.NutritionStatsResponse;
import com.s206.health.nutrition.statistics.service.MealStatisticsService;
import com.s206.health.nutrition.statistics.type.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/meals/statistics")
@RequiredArgsConstructor
@Slf4j
public class MealStatisticsController {

    private final MealStatisticsService mealStatisticsService;

    @GetMapping("/nutrition")
    public ResponseEntity<ResponseDto<NutritionStatsResponse>> getNutritionStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "DAY") PeriodType periodType,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        LocalDate targetDate = endDate != null ? endDate : LocalDate.now();
        log.info("Getting nutrition statistics: userId={}, endDate={}, periodType={}", userId, targetDate, periodType);

        NutritionStatsResponse response = mealStatisticsService.getNutritionStats(userId, targetDate, periodType);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "영양 통계 조회 성공", response));
    }
}