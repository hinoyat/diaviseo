package com.example.diaviseo.network.meal.dto.res

// 아침, 점심, 저녁, 간식의 식단 응답형태
data class MealTimeNutritionResponse(
    val mealTimeId: Int?,
    val mealType: String,
    val eatingTime: String,
    val foods: List<MealFoodResponse>,
    val nutrition: MealNutritionResponse,
    val createdAt: String,
    val updatedAt: String,
    val mealTimeImageUrl: String?
)

