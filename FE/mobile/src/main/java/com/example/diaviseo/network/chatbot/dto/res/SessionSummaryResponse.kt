package com.example.diaviseo.network.chatbot.dto.res

data class SessionSummaryResponse(
    val session_id: String,
    val chatbot_type: String,
    val started_at: String,
    val ended_at: String?,     // null 허용
    val is_saved: Boolean?     // null 허용
)
