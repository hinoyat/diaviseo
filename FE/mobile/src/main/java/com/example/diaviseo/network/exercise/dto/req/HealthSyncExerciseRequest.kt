package com.example.diaviseo.network.exercise.dto.req

// 헬스 커넥트 연동 (운동 등록)
data class HealthSyncExerciseRequest(
    val exerciseNumber: Int,
    val exerciseDate: String,
    val exerciseTime: Int,
    val exerciseCalorie: Int
)
