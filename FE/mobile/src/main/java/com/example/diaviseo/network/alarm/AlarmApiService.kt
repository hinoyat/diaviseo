package com.example.diaviseo.network.alarm

import com.example.diaviseo.network.common.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AlarmApiService {

    @POST("users/fcm-token")
    suspend fun sendFcmToken(@Body request: Map<String, String>): ApiResponse<Unit>
}