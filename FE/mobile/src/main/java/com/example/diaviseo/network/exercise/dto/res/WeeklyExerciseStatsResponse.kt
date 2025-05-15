package com.example.diaviseo.network.exercise.dto.res

data class WeeklyExerciseStatsResponse(
    val weeklyExercises: List<WeeklyExercise>
) {
    data class WeeklyExercise(
        val startDate: String,
        val endDate: String,
        val avgDailyCalories: Double,
        val totalExerciseCount: Int?,
        val totalCalories: Int?
    )
}