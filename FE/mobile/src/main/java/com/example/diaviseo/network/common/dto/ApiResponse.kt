package com.example.diaviseo.network.common.dto

// 백엔드 공통 응답 형식
data class ApiResponse<T>(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: T?
)