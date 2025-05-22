package com.s206.health.nutrition.food.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.nutrition.favorite.repository.FavoriteFoodRepository;
import com.s206.health.nutrition.food.dto.response.FoodDetailResponse;
import com.s206.health.nutrition.food.dto.response.FoodListResponse;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;

    @Transactional(readOnly = true)
    public List<FoodListResponse> getFoodList(Integer userId) {
        return foodRepository.findAll().stream()
                .map(food -> FoodListResponse.toDto(food,
                        favoriteFoodRepository.existsByUserIdAndFoodFoodId(userId, food.getFoodId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(Integer foodId, Integer userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundException("해당 음식을 찾을 수 없습니다."));
        boolean isFavorite = favoriteFoodRepository.existsByUserIdAndFoodFoodId(userId, foodId);
        return FoodDetailResponse.toDto(food, isFavorite);
    }
}
