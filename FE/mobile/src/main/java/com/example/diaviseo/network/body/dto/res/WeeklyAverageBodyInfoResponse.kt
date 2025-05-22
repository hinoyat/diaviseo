package com.example.diaviseo.network.body.dto.res

data class WeeklyAverageBodyInfoResponse(
    val weekLabel: Int,
    val avgWeight: Double,
    val avgMuscleMass: Double,
    val avgBodyFat: Double,
    val avgHeight: Double
)