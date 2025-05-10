package com.example.diaviseo.network.exercise.dto.res

data class ExerciseRecordResponse(
    val exerciseId: Int,
    val userId: Int,
    val exerciseTypeId: Int,
    val exerciseName: String,
    val exerciseCategoryName: String,
    val exerciseDate: String,
    val exerciseTime: Int,
    val exerciseCalorie: Int,
    val createdAt: String
)
