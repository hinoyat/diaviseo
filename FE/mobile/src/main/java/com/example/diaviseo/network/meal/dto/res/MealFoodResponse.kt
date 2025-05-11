package com.example.diaviseo.network.meal.dto.res


// 무슨 음식? 어떤 영양성분?에 대한 응답형식
data class MealFoodResponse(
    val mealFoodId: Int?,
    val foodId: Int?,
    val foodName: String,
    val calorie: Int?,
    val carbohydrate: Double,
    val protein: Double?,
    val fat: Double?,
    val sugar: Double?,
    val sodium: Double?,
    val quantity: Int?,
    val foodImageUrl: String?,
    val totalCalorie: Int,
    val totalCarbohydrate: Double?,
    val totalProtein: Double?,
    val totalFat: Double?,
    val totalSugar: Double?,
    val totalSodium: Double?
)
