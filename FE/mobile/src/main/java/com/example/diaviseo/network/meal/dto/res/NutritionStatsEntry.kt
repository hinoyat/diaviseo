package com.example.diaviseo.network.meal.dto.res

data class NutritionStatsEntry(
    val label: String,
    val calorie: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val sugar: Int,
    val total: Int?
)