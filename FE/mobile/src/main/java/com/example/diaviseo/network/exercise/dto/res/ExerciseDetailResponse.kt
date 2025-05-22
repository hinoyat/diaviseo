package com.example.diaviseo.network.exercise.dto.res

data class ExerciseDetailResponse(
    val exerciseTypeId: Int,
    val exerciseCategoryId: Int,
    val exerciseName: String,
    val exerciseEnglishName: String,
    val exerciseCalorie: Int,
    val exerciseNumber: Int,
    val exerciseCategoryName: String,
    val isFavorite: Boolean,
    val createdAt: String
)
