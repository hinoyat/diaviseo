package com.example.diaviseo.network.chatbot.dto.res

data class WeightPredictionResponse(
    val base_date: String?,  // "2025-05-19"
    val prediction_date: String?,  // "2025-06-18"
    val predicted_weight: PredictedWeight?,  // null이면 실패 응답
    val days_ahead: Int?,  // 30
    val error: String?,  // 실패 응답에만 존재
    val message: String?  // 실패 응답에만 존재
)

data class PredictedWeight(
    val projected_change: Double,  // -9.7
    val days_tracked: Int,
    val status: String  // "감량 예상"
)
