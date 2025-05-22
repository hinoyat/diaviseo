package com.example.diaviseo.mapper

import com.example.diaviseo.model.diet.FoodWithQuantity
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse

fun FoodWithQuantity.toFoodDetailResponse(): FoodDetailResponse {
    return FoodDetailResponse(
        foodId = this.foodId,
        foodName = this.foodName,
        calorie = this.calorie,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat,
        sweet = this.sweet,
        sodium = 0.0,
        saturatedFat = 0.0,
        transFat = 0.0,
        cholesterol = 0.0,
        isFavorite = false,
        baseAmount = "1"
    )
}
