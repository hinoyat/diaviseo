package com.s206.health.nutrition.meal.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.meal.dto.request.MealCreateRequest;
import com.s206.health.nutrition.meal.dto.request.MealTimeRequest;
import com.s206.health.nutrition.meal.dto.response.DailyNutritionResponse;
import com.s206.health.nutrition.meal.dto.response.MealDailyResponse;
import com.s206.health.nutrition.meal.dto.response.MealDetailResponse;
import com.s206.health.nutrition.meal.dto.response.MealTimeResponse;
import com.s206.health.nutrition.meal.dto.response.WeeklyNutritionResponse;
import com.s206.health.nutrition.meal.entity.MealType;
import com.s206.health.nutrition.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    // 1. 식단 등록/수정 API - 날짜 기반 처리
    @PostMapping
    public ResponseEntity<ResponseDto<MealDetailResponse>> createOrUpdateMeal(
            @RequestBody MealCreateRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Creating/Updating meal by userId={} for date={}", userId, request.getMealDate());
        MealDetailResponse response = mealService.createOrUpdateMeal(request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "식단 등록/수정 성공", response));
    }

    // 2. 식단 상세 조회 API - mealId
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<MealDetailResponse>> getMealDetail(
            @PathVariable("id") Integer mealId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting meal detail: mealId={}, userId={}", mealId, userId);
        MealDetailResponse response = mealService.getMealDetail(mealId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "식단 상세 조회 성공", response));
    }

    // 3. 당일 식단 조회 API - 영양정보 포함
    @GetMapping("/today")
    public ResponseEntity<ResponseDto<MealDailyResponse>> getTodayMeal(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting today's meal for userId={}", userId);
        LocalDate today = LocalDate.now();
        MealDailyResponse response = mealService.getMealByDate(today, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "당일 식단 조회 성공", response));
    }

    // 4. 일일 영양정보 조회 API (기존 유지)
    @GetMapping("/daily-nutrition")
    public ResponseEntity<ResponseDto<DailyNutritionResponse>> getDailyNutrition(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("Getting daily nutrition for userId={}, date={}", userId, targetDate);
        DailyNutritionResponse response = mealService.getDailyNutrition(userId, targetDate);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "일일 영양정보 조회 성공", response));
    }

    // 5. 주간 영양정보 조회 API
    @GetMapping("/weekly-nutrition")
    public ResponseEntity<ResponseDto<WeeklyNutritionResponse>> getWeeklyNutrition(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        LocalDate targetDate = endDate != null ? endDate : LocalDate.now();
        log.info("Getting weekly nutrition for userId={}, endDate={}", userId, targetDate);
        WeeklyNutritionResponse response = mealService.getWeeklyNutrition(userId, targetDate);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "주간 영양정보 조회 성공", response));
    }

    // 6. 식단 삭제 API  - mealId 기반
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteMeal(
            @PathVariable("id") Integer mealId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal: mealId={}, userId={}", mealId, userId);
        mealService.deleteMeal(mealId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "식단 삭제 성공"));
    }

    // 7. 날짜별 식단 조회 API
    @GetMapping("/date/{date}")
    public ResponseEntity<ResponseDto<MealDailyResponse>> getMealByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting meal for date: date={}, userId={}", date, userId);
        MealDailyResponse response = mealService.getMealByDate(date, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "날짜별 식단 조회 성공", response));
    }

    // 8. 날짜별 식단 삭제 API
    @DeleteMapping("/date/{date}")
    public ResponseEntity<ResponseDto<Void>> deleteMealByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal by date: date={}, userId={}", date, userId);
        mealService.deleteMealByDate(date, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "날짜별 식단 삭제 성공"));
    }

    // 9. 시간대별 식단 삭제 API
    @DeleteMapping("/date/{date}/type/{mealType}")
    public ResponseEntity<ResponseDto<Void>> deleteMealTimeByDateAndType(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("mealType") MealType mealType,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal time: date={}, type={}, userId={}", date, mealType, userId);
        mealService.deleteMealTimeByDateAndType(date, mealType, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "시간대별 식단 삭제 성공"));
    }

    // 10. 개별 음식 삭제 API
    @DeleteMapping("/food/{mealFoodId}")
    public ResponseEntity<ResponseDto<Void>> deleteMealFood(
            @PathVariable("mealFoodId") Integer mealFoodId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal food: mealFoodId={}, userId={}", mealFoodId, userId);
        mealService.deleteMealFood(mealFoodId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 삭제 성공"));
    }

    // 11. 시간대별 수정 API - 사용 미정
    @PutMapping("/date/{date}/type/{mealType}")
    public ResponseEntity<ResponseDto<MealTimeResponse>> updateMealTimeByDateAndType(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("mealType") MealType mealType,
            @RequestBody MealTimeRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Updating meal time: date={}, type={}, userId={}", date, mealType, userId);
        MealTimeResponse response = mealService.updateMealTimeByDateAndType(date, mealType, request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "시간대별 식단 수정 성공", response));
    }
}