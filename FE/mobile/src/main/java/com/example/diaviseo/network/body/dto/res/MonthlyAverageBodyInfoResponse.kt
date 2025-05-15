package com.example.diaviseo.network.body.dto.res

data class MonthlyAverageBodyInfoResponse(
    val monthIndex: Int,  // 1~7, 필수 값이므로 non-nullable
    val avgWeight: Double,
    val avgMuscleMass: Double,
    val avgBodyFat: Double,
    val avgHeight: Double
)
