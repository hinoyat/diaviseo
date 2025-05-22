package com.example.diaviseo.network.body.dto.res

data class BodyInfoResponse(
    val bodyId: Int?,
    val userId: Int,
    val height: Double,
    val weight: Double,
    val bodyFat: Double,
    val muscleMass: Double,
    val createdAt: String?,
    val measurementDate: String?,
    val bmi: Double,
    val bmr: Double
)
