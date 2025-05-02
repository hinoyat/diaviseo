package com.s206.health.nutrition.food.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.food.dto.response.FoodDetailResponse;
import com.s206.health.nutrition.food.dto.response.FoodListResponse;
import com.s206.health.nutrition.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foods")
@Slf4j
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<FoodListResponse>>> getFoodList(
            @RequestHeader("X-USER-ID") Integer userId) {

        log.info("음식 전체 목록 요청 - userId={}", userId);
        List<FoodListResponse> foodList = foodService.getFoodList(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 전체 조회 성공", foodList));
    }

    @GetMapping("/{foodId}")
    public ResponseEntity<ResponseDto<FoodDetailResponse>> getFoodDetail(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer foodId) {

        log.info("음식 상세 조회 요청 - userId={}, foodId={}", userId, foodId);
        FoodDetailResponse response = foodService.getFoodDetail(foodId, userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "음식 상세 조회 성공", response));
    }
}