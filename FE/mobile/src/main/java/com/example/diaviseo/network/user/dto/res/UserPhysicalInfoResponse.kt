package com.example.diaviseo.network.user.dto.res

data class UserPhysicalInfoResponse(
    val height: Double,
    val weight: Double,
    val birthday: String,
    val goal: String,
    val date: String,
    val age: Int,
    val bmr: Int,
    val tdee: Int,
    val recommendedIntake: Int,
    val recommendedExercise: Int
)