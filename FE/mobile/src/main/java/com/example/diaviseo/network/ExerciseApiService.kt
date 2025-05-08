package com.example.diaviseo.network

data class ExerciseRecordRequest(
    val exerciseTypeId: Int,         // 헬스 커넥트에서 받아온 운동 번호
    val exerciseDate: String,        // 운동 일자 + 시간
    val exerciseTime: Int,           // 운동 시간 (단위: 분)
)