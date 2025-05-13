package com.example.diaviseo.mapper

import com.example.diaviseo.network.food.dto.res.FavoriteFoodItemResponse
import com.example.diaviseo.network.food.dto.res.FoodItem

fun FavoriteFoodItemResponse.toFoodItem(): FoodItem {
    return FoodItem(
        foodId = this.foodId,
        foodName = this.foodName,
        calorie = this.calorie,
        carbohydrate = 0.0, // 기본값
        protein = 0.0,      // 기본값
        fat = 0.0,          // 기본값
        sweet = 0.0,        // 기본값
        baseAmount = "100g", // 기본 문자열
        isFavorite = true   // 즐겨찾기 목록이므로 true
    )
}
