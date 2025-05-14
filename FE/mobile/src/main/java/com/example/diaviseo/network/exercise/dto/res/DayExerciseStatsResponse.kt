package com.example.diaviseo.network.exercise.dto.res

data class DayExerciseStatsResponse(
    val date: String,
    val totalCalories: Int?,
    val totalExerciseTime: Int?,
    val exerciseCount: Int?,
    val exercises: List<ExerciseDetail>
) {
    data class ExerciseDetail(
        val exerciseId: Int,
        val exerciseName: String,
        val categoryName: String,
        val exerciseDate: String,
        val exerciseTime: Int,
        val exerciseCalorie: Int
    )
}
