package com.example.diaviseo.network

import retrofit2.http.GET
import retrofit2.Response


// 요청할 때 보낼 데이터
data class GoogleLoginRequest(
    val idToken: String
)

// 서버 응답받을 데이터
data class GoogleLoginResponse(
    val userId: Number,
    val id: Number,
    val title: String,
    val completed: Boolean
)

interface AuthApiService {
//    @GET("/auth/oauth/google")
    @GET("/todos/1")
    suspend fun loginWithGoogle(
    ): Response<GoogleLoginResponse>
}
