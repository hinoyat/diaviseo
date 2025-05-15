package com.example.diaviseo.network.exercise.dto.res

data class StepWeeklyResponse(
    val weeklySteps: List<StepDailyData>,
    val startDate: String,
    val endDate: String,
    val totalSteps: Int,
    val avgSteps: Int
) {
    data class StepDailyData(
        val date: String,
        val stepCount: Int
    )
}