package com.example.diaviseo.network.chatbot.dto.res

data class ChatMessageResponse(
    val _id: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String,
    val session_id: String
)
