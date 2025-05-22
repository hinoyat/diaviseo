package com.example.diaviseo.network.meal.dto.res

import java.math.BigDecimal
import java.time.LocalDate

data class DailyNutritionResponse(
    val date: String,
    val totalCalorie: Int,
    val totalCarbohydrate: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalSugar: Double,
    val totalSodium: Double
)