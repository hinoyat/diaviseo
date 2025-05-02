package com.s206.health.nutrition.favorite.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.nutrition.favorite.dto.response.FavoriteFoodResponse;
import com.s206.health.nutrition.favorite.dto.response.FavoriteToggleResponse;
import com.s206.health.nutrition.favorite.entity.FavoriteFood;
import com.s206.health.nutrition.favorite.repository.FavoriteFoodRepository;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public FavoriteToggleResponse toggleFavorite(Integer userId, Integer foodId) {
        FavoriteFood favorite = favoriteFoodRepository.findByUserIdAndFoodFoodId(userId, foodId).orElse(null);
        // TODO: ifPresent or map 등을 활용해 코드 흐름 개선

        if (favorite != null) {
            favoriteFoodRepository.delete(favorite);
            return FavoriteToggleResponse.toDto(favorite, false);
        }

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundException("해당 음식을 찾을 수 없습니다."));

        FavoriteFood newFavorite = favoriteFoodRepository.save(FavoriteFood.builder()
                .userId(userId)
                .food(food)
                .build());

        return FavoriteToggleResponse.toDto(newFavorite, true);
    }

    @Transactional(readOnly = true)
    public List<FavoriteFoodResponse> getFavoriteFoods(Integer userId) {
        return favoriteFoodRepository.findAllByUserId(userId).stream()
                .map(FavoriteFoodResponse::toDto)
                .collect(Collectors.toList());
    }
}