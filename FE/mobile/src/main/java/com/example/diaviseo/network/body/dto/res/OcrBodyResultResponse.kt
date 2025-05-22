package com.example.diaviseo.network.body.dto.res

data class OcrBodyResultResponse(
    val weight: Double,
    val bodyFat: Double,
    val muscleMass: Double,
    val height: Double,
    val measurementDate: String // 예: "2022-02-24"
)
