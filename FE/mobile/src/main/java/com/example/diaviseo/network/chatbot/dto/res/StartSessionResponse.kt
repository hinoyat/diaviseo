package com.example.diaviseo.network.chatbot.dto.res

data class StartSessionResponse(
    val session_id: String,
    val welcome_message: String
)


/* 에러 메세지
*
{
  "detail": "X-USER-ID 헤더가 필요합니다"
}

* */