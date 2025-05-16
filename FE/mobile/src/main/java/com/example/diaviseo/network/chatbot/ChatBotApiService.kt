package com.example.diaviseo.network.chatbot

import com.example.diaviseo.network.chatbot.dto.req.ChatRequest
import com.example.diaviseo.network.chatbot.dto.req.EndSessionRequest
import com.example.diaviseo.network.chatbot.dto.req.StartSessionRequest
import com.example.diaviseo.network.chatbot.dto.res.ChatMessageResponse
import com.example.diaviseo.network.chatbot.dto.res.ChatResponse
import com.example.diaviseo.network.chatbot.dto.res.EndSessionResponse
import com.example.diaviseo.network.chatbot.dto.res.SessionSummaryResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatBotApiService {
    @POST("chatbot/start-session")
    suspend fun startSession(
        @Body request: StartSessionRequest
    ):ApiResponse<StartSessionRequest>


    @POST("chatbot/chat/{sessionId}")
    suspend fun sendChatMessage(
        @Path("sessionId") sessionId: String,
        @Body request: ChatRequest
    ): ChatResponse

    @GET("chatbot/chats")
    suspend fun getChatSessions(): List<SessionSummaryResponse>

    @GET("chatbot/messages/{sessionId}")
    suspend fun getChatMessages(
        @Path("sessionId") sessionId: String
    ): List<ChatMessageResponse>

    @POST("chatbot/end-session")
    suspend fun endSession(
        @Body request: EndSessionRequest
    ): EndSessionResponse


}
