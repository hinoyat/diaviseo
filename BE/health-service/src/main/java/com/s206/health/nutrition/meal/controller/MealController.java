package com.s206.health.nutrition.meal.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.food.dto.response.FoodDetailResponse;
import com.s206.health.nutrition.food.dto.response.FoodListResponse;
import com.s206.health.nutrition.meal.dto.request.MealCreateRequest;
import com.s206.health.nutrition.meal.dto.request.MealTimeRequest;
import com.s206.health.nutrition.meal.dto.response.*;
import com.s206.health.nutrition.meal.entity.MealType;
import com.s206.health.nutrition.meal.service.MealImageService;
import com.s206.health.nutrition.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Slf4j
public class MealController {

    private final MealService mealService;
    private final MealImageService mealImageService;

    // 1. 식단 등록/수정 API - 날짜 기반 처리
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<MealDetailResponse>> createOrUpdateMealWithImages(
            @RequestPart("mealData") MealCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Creating/Updating meal with images by userId={} for date={}", userId, request.getMealDate());
        MealDetailResponse response = mealService.createOrUpdateMealWithImages(request, images, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "식단 및 이미지 등록/수정 성공", response));
    }

    // 2. 식단 상세 조회 API - mealId
    @GetMapping("/{mealId}")
    public ResponseEntity<ResponseDto<MealDetailResponse>> getMealDetail(
            @PathVariable("mealId") Integer mealId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting meal detail: mealId={}, userId={}", mealId, userId);
        MealDetailResponse response = mealService.getMealDetail(mealId, userId);

        // 이미지 URL 처리
        for (MealTimeResponse mealTimeResponse : response.getMealTimes()) {
            // mealTimeImageUrl 처리
            String mealTimeImageUrl = mealTimeResponse.getMealTimeImageUrl();
            if (mealTimeImageUrl != null && !mealTimeImageUrl.isEmpty()) {
                mealTimeResponse.setMealTimeImageUrl(mealImageService.getMealImageUrl(mealTimeImageUrl));
            }

            // foodImageUrl 처리
            for (MealFoodResponse foodResponse : mealTimeResponse.getFoods()) {
                String imageUrl = foodResponse.getFoodImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    foodResponse.setFoodImageUrl(mealImageService.getMealImageUrl(imageUrl));
                }
            }
        }

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
    @DeleteMapping("/{mealId}")
    public ResponseEntity<ResponseDto<Void>> deleteMeal(
            @PathVariable("mealId") Integer mealId,
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

    // 12. 음식 이미지 업로드 API
    @PostMapping(value = "/food/{mealFoodId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Map<String, String>>> uploadFoodImage(
            @PathVariable("mealFoodId") Integer mealFoodId,
            @RequestParam("image") MultipartFile file,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Uploading food image: mealFoodId={}, userId={}", mealFoodId, userId);
        Map<String, String> response = mealService.uploadMealFoodImage(mealFoodId, file, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "음식 이미지 업로드 성공", response));
    }

    @GetMapping("/recent-foods")
    public ResponseEntity<ResponseDto<List<FoodDetailResponse>>> getRecentFoods(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting recent foods: userId={}, limit={}", userId, 10);
        List<FoodDetailResponse> response = mealService.getRecentFoods(userId, 10);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "최근 먹은 음식 조회 성공", response));
    }
}