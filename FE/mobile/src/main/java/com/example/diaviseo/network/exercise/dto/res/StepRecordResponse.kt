package com.example.diaviseo.network.exercise.dto.res

data class StepRecordResponse(
    val stepCountId: Int,
    val userId: Int,
    val stepDate: String,   // "yyyy-MM-dd"
    val stepCount: Int,
    val createdAt: String
)
