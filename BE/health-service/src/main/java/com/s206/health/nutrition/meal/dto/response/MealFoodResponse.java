package com.s206.health.nutrition.meal.dto.response;

import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.meal.entity.MealFood;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MealFoodResponse {
    private Integer mealFoodId;
    private Integer foodId;
    private String foodName;

    // 기본 영양 정보
    private Integer calorie;

    // 추가 영양 정보
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal sugar;
    private BigDecimal sodium;

    private Integer quantity;
    private String foodImageUrl;

    // 해당 음식의 수량을 고려한 총 영양소 정보
    private Integer totalCalorie;
    private BigDecimal totalCarbohydrate;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalSugar;
    private BigDecimal totalSodium;

    public static MealFoodResponse toDto(MealFood mealFood) {
        Food food = mealFood.getFood();
        Integer quantity = mealFood.getQuantity();

        // 수량을 고려한 총 영양소 계산
        Integer totalCalorie = food.getCalorie() * quantity;
        BigDecimal totalCarb = food.getCarbohydrate().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalProtein = food.getProtein().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalFat = food.getFat().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalSugar = food.getSweet().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalSodium = food.getSodium().multiply(BigDecimal.valueOf(quantity));

        return MealFoodResponse.builder()
                .mealFoodId(mealFood.getMealFoodId())
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .calorie(food.getCalorie())
                .carbohydrate(food.getCarbohydrate())
                .protein(food.getProtein())
                .fat(food.getFat())
                .sugar(food.getSweet())
                .sodium(food.getSodium())
                .quantity(quantity)
                .foodImageUrl(mealFood.getFoodImageUrl())
                .totalCalorie(totalCalorie)
                .totalCarbohydrate(totalCarb)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .totalSugar(totalSugar)
                .totalSodium(totalSodium)
                .build();
    }
}