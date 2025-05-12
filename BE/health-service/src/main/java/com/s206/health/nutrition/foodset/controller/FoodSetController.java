package com.s206.health.nutrition.foodset.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.foodset.dto.request.FoodSetRequest;
import com.s206.health.nutrition.foodset.dto.response.FoodSetDetailResponse;
import com.s206.health.nutrition.foodset.dto.response.FoodSetListResponse;
import com.s206.health.nutrition.foodset.service.FoodSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-sets")
@RequiredArgsConstructor
@Slf4j
public class FoodSetController {

    private final FoodSetService foodSetService;

    @PostMapping
    public ResponseEntity<ResponseDto<FoodSetDetailResponse>> createFoodSet(
            @RequestBody FoodSetRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Creating food set by userId={} with name={}", userId, request.getName());
        FoodSetDetailResponse response = foodSetService.createFoodSet(request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.CREATED, "음식 세트 등록 성공", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<FoodSetDetailResponse>> getFoodSetDetail(
            @PathVariable("id") Integer foodSetId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting food set detail: foodSetId={}, userId={}", foodSetId, userId);
        FoodSetDetailResponse response = foodSetService.getFoodSetDetail(foodSetId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 세트 상세 조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<FoodSetDetailResponse>>> getAllFoodSetsByUser(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Getting all food sets for userId={}", userId);
        List<FoodSetDetailResponse> response = foodSetService.getAllFoodSetsByUser(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 세트 목록 조회 성공", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<FoodSetDetailResponse>> updateFoodSet(
            @PathVariable("id") Integer foodSetId,
            @RequestBody FoodSetRequest request,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Updating food set: foodSetId={}, userId={}", foodSetId, userId);
        FoodSetDetailResponse response = foodSetService.updateFoodSet(foodSetId, request, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 세트 수정 성공", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteFoodSet(
            @PathVariable("id") Integer foodSetId,
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("Deleting food set: foodSetId={}, userId={}", foodSetId, userId);
        foodSetService.deleteFoodSet(foodSetId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 세트 삭제 성공"));
    }
}
