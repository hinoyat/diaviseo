package com.example.diaviseo.network.exercise.dto.res

data class MonthlyExerciseStatsResponse(
    val monthlyExercises: List<MonthlyExercise>
) {
    data class MonthlyExercise(
        val yearMonth: String,
        val avgDailyCalories: Double,
        val totalExerciseCount: Int?,
        val totalCalories: Int?
    )
}
