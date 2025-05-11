package com.example.diaviseo.network.exercise.dto.res

data class HealthSyncExerciseResponse(
    val exerciseId: Int,
    val userId: Int,
    val exerciseTypeId: Int,
    val exerciseNumber: Int,
    val exerciseName: String,
    val exerciseCategoryName: String,
    val exerciseDate: String,
    val exerciseTime: Int,
    val exerciseCalorie: Int,
    val createdAt: String
)

data class HealthSyncExerciseListResponse(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: List<HealthSyncExerciseResponse>
)
