package com.example.diaviseo.model.chat

import java.time.LocalDateTime

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime,
    val characterImageRes: Int? = null
)
