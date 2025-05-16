package com.example.diaviseo.network.chatbot.dto.res

data class ChatMessageResponse(
    val content: String,
    val type: String // "ai" or "human"
)
