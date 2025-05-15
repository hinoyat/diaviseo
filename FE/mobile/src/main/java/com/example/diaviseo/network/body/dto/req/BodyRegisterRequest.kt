package com.example.diaviseo.network.body.dto.req

data class BodyRegisterRequest(
    val weight: Double,
    val bodyFat: Double,
    val muscleMass: Double,
    val height: Double,
    val measurementDate: String // 예: "2025-05-11"
)
