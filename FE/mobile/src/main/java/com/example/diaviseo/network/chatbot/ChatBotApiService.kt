package com.example.diaviseo.network.chatbot

import com.example.diaviseo.network.chatbot.dto.req.ChatRequest
import com.example.diaviseo.network.chatbot.dto.req.EndSessionRequest
import com.example.diaviseo.network.chatbot.dto.req.StartSessionRequest
import com.example.diaviseo.network.chatbot.dto.res.ChatMessageResponse
import com.example.diaviseo.network.chatbot.dto.res.EndSessionResponse
import com.example.diaviseo.network.chatbot.dto.res.SessionSummaryResponse
import com.example.diaviseo.network.chatbot.dto.res.StartSessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ChatBotApiService {
    @POST("chatbot/start-session")
    suspend fun startSession(
        @Body request: StartSessionRequest
    ):StartSessionResponse


    @POST("chatbot/chat/{sessionId}")
    suspend fun sendChatMessage(
        @Path("sessionId") sessionId: String,
        @Body request: ChatRequest
    ): ChatMessageResponse

    @GET("chatbot/chats")
    suspend fun getChatSessions(): List<SessionSummaryResponse>

    @GET("chatbot/messages/{sessionId}")
    suspend fun getChatMessages(
        @Path("sessionId") sessionId: String
    ): List<ChatMessageResponse>

    @HTTP(method = "DELETE", path = "chatbot/end-session", hasBody = true)
    suspend fun endSession(
        @Body request: EndSessionRequest
    ): EndSessionResponse

    // 과거 피드백 내역 조회
    @GET("chatbot/feedback/{feedbackType}")
    suspend fun fetchFeedBack(
        @Path("feedbackType") feedbackType: String,
        @Query("date") date: String
    ): Response<String>

    // 식단 피드백 생성
    @GET("chatbot/nutrition_feedback")
    suspend fun createNutriFeedBack(
        @Query("date") date: String
    ): Response<Map<String, String>>
}
