package com.example.diaviseo.network.exercise.dto.res

import java.math.BigDecimal
import java.time.LocalDate

data class DailyNutritionResponse(
    val date: LocalDate,
    val totalCalorie: Int?,
    val totalCarbohydrate: BigDecimal?,
    val totalProtein: BigDecimal?,
    val totalFat: BigDecimal?,
    val totalSugar: BigDecimal?,
    val totalSodium: BigDecimal?
)
