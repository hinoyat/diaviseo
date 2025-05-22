package com.example.diaviseo.network.body.dto.res

data class BodyRegisterResponse(
    val bodyId: Long,
    val userId: Long,
    val weight: Double,
    val bodyFat: Double,
    val muscleMass: Double,
    val createdAt: String,
    val measurementDate: String
)
