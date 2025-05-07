package com.example.diaviseo.network

import retrofit2.http.*
import retrofit2.Response


// 요청할 때 보낼 데이터
data class GoogleLoginRequest(
    val provider: String,
    val idToken: String
)

data class TestLoginRequest(
    val email: String,
    val provider: String
)

data class PhoneAuthTryRequest(
    val phone: String,
)

data class PhoneAuthConfirmRequest(
    val phone: String,
    val code: String
)

// 서버 응답받을 데이터
// 그 중에서도 공통 데이터
data class ApiResponse<T>(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: T?
)

data class GoogleLoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val newUser: Boolean
)

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/oauth/login")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequest
    ): ApiResponse<GoogleLoginResponse>

    @POST("auth/test/login")
    suspend fun loginWithTest(
        @Body request: TestLoginRequest
    ): ApiResponse<GoogleLoginResponse>

    @POST("users/verify/phone")
    suspend fun phoneAuthTry(
        @Body request: PhoneAuthTryRequest
    ): ApiResponse<Unit>   // data에 null 올 거 알고 있음

    @POST("users/verify/phone/confirm")
    suspend fun phoneAuthConfirm(
        @Body request: PhoneAuthConfirmRequest
    ): ApiResponse<Unit>   // data에 null 올 거 알고 있음
}
