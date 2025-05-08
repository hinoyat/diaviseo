package com.s206.health.nutrition.meal.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.meal.service.MealImageService;
import com.s206.health.nutrition.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/meals/images")
@RequiredArgsConstructor
@Slf4j
public class MealImageController {

    private final MealImageService mealImageService;
    private final MealService mealService;

    /**
     * 식단 이미지 업로드 API
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Map<String, String>>> uploadMealImage(
            @RequestParam("image") MultipartFile file,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Uploading meal image for userId={}", userId);

        // 이미지 업로드
        String objectName = mealImageService.uploadMealImage(file);

        // 접근 URL 생성
        String imageUrl = mealImageService.getMealImageUrl(objectName);

        // 결과 반환
        Map<String, String> result = new HashMap<>();
        result.put("objectName", objectName);
        result.put("imageUrl", imageUrl);

        return ResponseEntity.ok(ResponseDto.success(
                HttpStatus.CREATED,
                "식단 이미지 업로드 성공",
                result));
    }

    /**
     * 특정 음식에 이미지 연결 API
     */
    @PostMapping("/meal-food/{mealFoodId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> attachImageToMealFood(
            @PathVariable Integer mealFoodId,
            @RequestParam("objectName") String objectName,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Attaching image to meal food: mealFoodId={}, objectName={}, userId={}",
                mealFoodId, objectName, userId);

        Map<String, String> result = mealService.attachImageToMealFood(mealFoodId, objectName, userId);

        return ResponseEntity.ok(ResponseDto.success(
                HttpStatus.OK,
                "이미지 연결 성공",
                result));
    }

    /**
     * 음식별 이미지 업로드 API
     */
    @PostMapping(value = "/meal-food/{mealFoodId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Map<String, String>>> uploadMealFoodImage(
            @PathVariable Integer mealFoodId,
            @RequestParam("image") MultipartFile file,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Uploading image for meal food: mealFoodId={}, userId={}", mealFoodId, userId);

        Map<String, String> result = mealService.uploadMealFoodImage(mealFoodId, file, userId);

        return ResponseEntity.ok(ResponseDto.success(
                HttpStatus.CREATED,
                "음식 이미지 업로드 성공",
                result));
    }

    /**
     * 이미지 URL 조회 API
     */
    @GetMapping("/{objectName}")
    public ResponseEntity<ResponseDto<Map<String, String>>> getMealImageUrl(
            @PathVariable String objectName,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting meal image URL for objectName={}, userId={}", objectName, userId);

        // 접근 URL 생성
        String imageUrl = mealImageService.getMealImageUrl(objectName);

        // 결과 반환
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", imageUrl);

        return ResponseEntity.ok(ResponseDto.success(
                HttpStatus.OK,
                "이미지 URL 조회 성공",
                result));
    }

    /**
     * 이미지 삭제 API
     */
    @DeleteMapping("/{objectName}")
    public ResponseEntity<ResponseDto<Void>> deleteMealImage(
            @PathVariable String objectName,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting meal image: objectName={}, userId={}", objectName, userId);

        // 이미지 삭제
        mealImageService.deleteMealImage(objectName);

        return ResponseEntity.ok(ResponseDto.success(
                HttpStatus.OK,
                "식단 이미지 삭제 성공"));
    }
}