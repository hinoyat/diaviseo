package com.example.diaviseo.network.exercise.dto.req

data class ExerciseRecordRequest(
    val exerciseNumber: Int,      // 운동 종류 넘버
    val exerciseDate: String?,    // 운동 시작 시간 (예: "2025-05-10T08:30:00"), null 가능
    val exerciseTime: Int         // 운동 시간 (단위: 분)
)
