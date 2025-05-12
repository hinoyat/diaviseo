package com.example.diaviseo.network.meal.dto.res

// 날짜별 식단 조회할 때 젤 처음 데이터 형식
data class MealDailyResponse(
    val mealId: Int?,
    val mealDate: String,
    val isMeal: Boolean?,
    val totalNutrition: MealNutritionResponse,
    val mealTimes: List<MealTimeNutritionResponse>,
    val createdAt: String,
    val updatedAt: String
)

