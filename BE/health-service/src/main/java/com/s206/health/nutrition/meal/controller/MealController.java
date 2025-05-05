package com.s206.health.nutrition.meal.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.meal.dto.request.MealCreateRequest;
import com.s206.health.nutrition.meal.dto.request.MealUpdateRequest;
import com.s206.health.nutrition.meal.dto.response.DailyNutritionResponse;
import com.s206.health.nutrition.meal.dto.response.MealDetailResponse;
import com.s206.health.nutrition.meal.dto.response.WeeklyNutritionResponse;
import com.s206.health.nutrition.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Slf4j
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<ResponseDto<MealDetailResponse>> createMeal(
            @RequestBody MealCreateRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Creating meal by userId={} for date={}", userId, request.getMealDate());
        MealDetailResponse response = mealService.createMeal(request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "식단 등록 성공", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<MealDetailResponse>> getMealDetail(
            @PathVariable("id") Integer mealId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting meal detail: mealId={}, userId={}", mealId, userId);
        MealDetailResponse response = mealService.getMealDetail(mealId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "식단 상세 조회 성공", response));
    }

    @GetMapping("/today")
    public ResponseEntity<ResponseDto<List<MealDetailResponse>>> getTodayMeals(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting today's meals for userId={}", userId);
        List<MealDetailResponse> response = mealService.getTodayMeals(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "당일 식단 조회 성공", response));
    }

    @GetMapping("/daily-nutrition")
    public ResponseEntity<ResponseDto<DailyNutritionResponse>> getDailyNutrition(
            @RequestParam(required = false) LocalDate date,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("Getting daily nutrition for userId={}, date={}", userId, targetDate);
        DailyNutritionResponse response = mealService.getDailyNutrition(userId, targetDate);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "일일 영양정보 조회 성공", response));
    }

    @GetMapping("/weekly-nutrition")
    public ResponseEntity<ResponseDto<WeeklyNutritionResponse>> getWeeklyNutrition(
            @RequestParam(required = false) LocalDate endDate,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        LocalDate targetDate = endDate != null ? endDate : LocalDate.now();
        log.info("Getting weekly nutrition for userId={}, endDate={}", userId, targetDate);
        WeeklyNutritionResponse response = mealService.getWeeklyNutrition(userId, targetDate);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "주간 영양정보 조회 성공", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<MealDetailResponse>> updateMeal(
            @PathVariable("id") Integer mealId,
            @RequestBody MealUpdateRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Updating meal: mealId={}, userId={}", mealId, userId);
        MealDetailResponse response = mealService.updateMeal(mealId, request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "식단 수정 성공", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteMeal(
            @PathVariable("id") Integer mealId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal: mealId={}, userId={}", mealId, userId);
        mealService.deleteMeal(mealId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "식단 삭제 성공"));
    }
}