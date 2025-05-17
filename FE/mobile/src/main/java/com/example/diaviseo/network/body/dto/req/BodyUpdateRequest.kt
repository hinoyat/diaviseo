package com.example.diaviseo.network.body.dto.req

data class BodyUpdateRequest(
    val weight: Double? = null,
    val bodyFat: Double? = null,
    val muscleMass: Double? = null,
    val height: Double? = null,
    val measurementDate: String
)
