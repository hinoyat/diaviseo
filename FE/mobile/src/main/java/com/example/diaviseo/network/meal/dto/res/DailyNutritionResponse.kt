package com.example.diaviseo.network.meal.dto.res

import java.math.BigDecimal
import java.time.LocalDate

data class DailyNutritionResponse(
    val date: String,
    val totalCalorie: Int?,
    val totalCarbohydrate: BigDecimal?,
    val totalProtein: BigDecimal?,
    val totalFat: BigDecimal?,
    val totalSugar: BigDecimal?,
    val totalSodium: BigDecimal?
)