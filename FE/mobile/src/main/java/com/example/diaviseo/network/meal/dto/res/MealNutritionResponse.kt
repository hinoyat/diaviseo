package com.example.diaviseo.network.meal.dto.res

import java.math.BigDecimal

// 식단 조회할 때의 영양성분
data class MealNutritionResponse(
    val totalCalorie: Int,
    val totalCarbohydrate: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalSugar: Double,
    val totalSodium: Double
)

