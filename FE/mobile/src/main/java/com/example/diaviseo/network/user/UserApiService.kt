package com.example.diaviseo.network.user

import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.user.dto.req.PhoneAuthConfirmRequest
import com.example.diaviseo.network.user.dto.req.PhoneAuthTryRequest
import com.example.diaviseo.network.user.dto.req.SignUpWithDiaRequest
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import com.example.diaviseo.network.user.dto.res.SignUpWithDiaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {
    @POST("users/verify/phone")
    suspend fun phoneAuthTry(
        @Body request: PhoneAuthTryRequest
    ): ApiResponse<Unit>   // data에 null 올 거 알고 있음

    @POST("users/verify/phone/confirm")
    suspend fun phoneAuthConfirm(
        @Body request: PhoneAuthConfirmRequest
    ): ApiResponse<Unit>   // data에 null 올 거 알고 있음

    @POST("users/signup")
    suspend fun signUpWithDia(
        @Body request: SignUpWithDiaRequest
    ): ApiResponse<SignUpWithDiaResponse>

    @GET("users/me")
    suspend fun fetchMyProfile(
    ): ApiResponse<FetchProfileResponse>
}