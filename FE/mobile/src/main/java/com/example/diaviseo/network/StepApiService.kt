package com.example.diaviseo.network

data class StepRecordRequest(
    val stepCount: Int,     // 걸음 수
    val date: String        // 날짜 (yyyy-MM-dd)
)