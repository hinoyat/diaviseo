package com.example.diaviseo.mapper

import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.network.food.dto.res.FoodItem

fun FoodDetailResponse.toFoodItem(): FoodItem {
    return FoodItem(
        foodId = this.foodId,
        foodName = this.foodName,
        calorie = this.calorie,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat,
        sweet = this.sweet,
        baseAmount = this.baseAmount,
        isFavorite = this.isFavorite
    )
}