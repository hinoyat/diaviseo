package com.example.diaviseo.network.meal.dto.res

data class NutritionStatsResponse(
    val startDate: String,
    val endDate: String,
    val periodType: String,
    val data: List<NutritionStatsEntry>
)