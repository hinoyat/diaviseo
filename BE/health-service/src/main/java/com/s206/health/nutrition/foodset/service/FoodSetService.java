package com.s206.health.nutrition.foodset.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.common.exception.types.UnauthorizedException;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import com.s206.health.nutrition.foodset.dto.request.FoodSetRequest;
import com.s206.health.nutrition.foodset.dto.response.FoodSetDetailResponse;
import com.s206.health.nutrition.foodset.dto.response.FoodSetListResponse;
import com.s206.health.nutrition.foodset.entity.FoodSet;
import com.s206.health.nutrition.foodset.entity.FoodSetFood;
import com.s206.health.nutrition.foodset.repository.FoodSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodSetService {

    private final FoodSetRepository foodSetRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public FoodSetDetailResponse createFoodSet(FoodSetRequest request, Integer userId) {
        log.info("[CREATE] userId={} → 음식 세트 등록 요청: {}", userId, request.getName());

        FoodSet foodSet = FoodSet.builder()
                .userId(userId)
                .name(request.getName())
                .build();

        List<FoodSetFood> foodSetFoods = request.getFoods().stream()
                .map(item -> {
                    Food food = foodRepository.findById(item.getFoodId())
                            .orElseThrow(() -> new BadRequestException("유효하지 않은 음식 ID입니다: " + item.getFoodId()));

                    return FoodSetFood.builder()
                            .foodSet(foodSet)
                            .food(food)
                            .quantity(item.getQuantity())
                            .build();
                }).toList();

        foodSet.getFoodSetFoods().addAll(foodSetFoods);
        foodSetRepository.save(foodSet);

        log.info("[CREATE] 세트 등록 완료: foodSetId={}", foodSet.getFoodSetId());
        return FoodSetDetailResponse.toDto(foodSet);
    }

    @Transactional(readOnly = true)
    public FoodSetDetailResponse getFoodSetDetail(Integer foodSetId, Integer userId) {
        log.info("[DETAIL] userId={} → 세트 상세 조회 요청: foodSetId={}", userId, foodSetId);

        FoodSet foodSet = findOwnedFoodSet(foodSetId, userId);

        log.info("[DETAIL] 조회 성공: foodSetId={}, name={}", foodSetId, foodSet.getName());
        return FoodSetDetailResponse.toDto(foodSet);
    }

    @Transactional(readOnly = true)
    public List<FoodSetListResponse> getAllFoodSetsByUser(Integer userId) {
        log.info("[LIST] userId={} → 음식 세트 목록 조회 요청", userId);

        return foodSetRepository.findAll().stream()
                .filter(fs -> fs.getUserId().equals(userId))
                .map(FoodSetListResponse::toDto)
                .toList();
    }

    @Transactional
    public FoodSetDetailResponse updateFoodSet(Integer foodSetId, FoodSetRequest request, Integer userId) {
        log.info("[UPDATE] userId={} → 세트 수정 요청: foodSetId={}", userId, foodSetId);

        FoodSet foodSet = findOwnedFoodSet(foodSetId, userId);

        foodSet.getFoodSetFoods().clear();
        foodSet.updateName(request.getName());

        List<FoodSetFood> newFoods = request.getFoods().stream()
                .map(item -> {
                    Food food = foodRepository.findById(item.getFoodId())
                            .orElseThrow(() -> new BadRequestException("유효하지 않은 음식 ID입니다: " + item.getFoodId()));
                    return FoodSetFood.builder()
                            .foodSet(foodSet)
                            .food(food)
                            .quantity(item.getQuantity())
                            .build();
                }).toList();

        foodSet.getFoodSetFoods().addAll(newFoods);
        foodSetRepository.save(foodSet);

        log.info("[UPDATE] 세트 수정 완료: foodSetId={}", foodSetId);
        return FoodSetDetailResponse.toDto(foodSet);
    }

    @Transactional
    public void deleteFoodSet(Integer foodSetId, Integer userId) {
        log.info("[DELETE] userId={} → 세트 삭제 요청: foodSetId={}", userId, foodSetId);

        FoodSet foodSet = findOwnedFoodSet(foodSetId, userId);
        foodSetRepository.delete(foodSet);

        log.info("[DELETE] 삭제 완료: foodSetId={}", foodSetId);
    }

    private FoodSet findOwnedFoodSet(Integer foodSetId, Integer userId) {
        FoodSet foodSet = foodSetRepository.findById(foodSetId)
                .orElseThrow(() -> new NotFoundException("음식 세트를 찾을 수 없습니다."));

        if (!foodSet.getUserId().equals(userId)) {
            throw new UnauthorizedException("세트에 접근할 권한이 없습니다.");
        }

        return foodSet;
    }
}
