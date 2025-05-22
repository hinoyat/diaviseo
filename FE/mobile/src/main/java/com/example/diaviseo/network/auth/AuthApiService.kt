package com.example.diaviseo.network.auth

import com.example.diaviseo.network.auth.dto.req.GoogleLoginRequest
import com.example.diaviseo.network.auth.dto.res.GoogleLoginResponse
import com.example.diaviseo.network.auth.dto.res.RefreshAuthTokenResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import retrofit2.http.*

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/oauth/login")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequest
    ): ApiResponse<GoogleLoginResponse>

    @POST("auth/reissue")
    suspend fun refreshAuthToken(
    ): ApiResponse<RefreshAuthTokenResponse>

    @POST("auth/logout")
    suspend fun logoutWithTokens(
        @Header("Authorization") accessToken: String,
        @Header("Refresh-Token") refreshToken: String
    ): ApiResponse<Unit>

}
