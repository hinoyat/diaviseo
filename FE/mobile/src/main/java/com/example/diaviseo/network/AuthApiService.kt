package com.example.diaviseo.network

import retrofit2.http.*
import retrofit2.Response


// 요청할 때 보낼 데이터
data class GoogleLoginRequest(
    val provider: String,
    val idToken: String
)

// 서버 응답받을 데이터
data class GoogleLoginResponse(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: GoogleLoginResponseData?
)

data class GoogleLoginResponseData(
    val accessToken: String?,
    val refreshToken: String?,
    val newUser: Boolean
)

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/oauth/login")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequest
    ): Response<GoogleLoginResponse>
}
