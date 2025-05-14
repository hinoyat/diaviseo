package com.example.diaviseo.network.exercise.dto.res

data class DailyExerciseStatsResponse(
    val dailyExercises: List<DailyExercise>
) {
    data class DailyExercise(
        val date: String,
        val totalCalories: Int,
        val exerciseCount: Int,
        val exercises: List<ExerciseDetail>
    )

    data class ExerciseDetail(
        val exerciseId: Int,
        val exerciseName: String,
        val categoryName: String,
        val exerciseTime: Int,
        val exerciseCalorie: Int
    )
}