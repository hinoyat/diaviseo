package com.example.diaviseo.network.exercise.dto.res

data class FavoriteExerciseResponse(
    val exerciseTypeId: Int,
    val exerciseNumber: Int,
    val exerciseName: String,
    val calorie: Int,
    val categoryName: String,
    val createdAt: String
)