package com.example.diaviseo.network.exercise.dto.res

data class RecentExerciseResponse(
    val exerciseTypeId: Int,
    val exerciseCategoryId: Int,
    val exerciseName: String,
    val exerciseEnglishName: String,
    val exerciseCalorie: Int,
    val exerciseNumber: Int,
    val createdAt: String
)