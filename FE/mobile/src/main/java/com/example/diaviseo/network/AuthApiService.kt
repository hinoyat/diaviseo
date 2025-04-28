package com.example.diaviseo.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query

// 요청할 때 보낼 데이터
data class GoogleLoginRequest(
    val idToken: String
)

// 서버 응답받을 데이터
data class GoogleLoginResponse(
    val userId: Number,
    val id: Number,
    val title: String,
    val name: Boolean
)

interface AuthApiService {
//    @GET("/auth/oauth/google")
    @GET("/post")
//    suspend fun loginWithGoogle(
    fun loginWithGoogle(
        @Query("idToken") idToken: String
    ): Response<GoogleLoginResponse>
}
