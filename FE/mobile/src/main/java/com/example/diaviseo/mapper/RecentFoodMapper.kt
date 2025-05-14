package com.example.diaviseo.mapper

import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse

fun RecentFoodItemResponse.toFoodItem(): FoodItem {
    return FoodItem(
        foodId = this.foodId.toInt(),
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