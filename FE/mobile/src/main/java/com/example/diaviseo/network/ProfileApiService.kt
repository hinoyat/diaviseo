package com.example.diaviseo.network

import retrofit2.http.*

// 서버 응답받을 데이터
// 그 중에서도 공통 데이터 => AuthApiService에 존재

data class FetchProfileResponse(
    val name: String,
    val nickname: String,
    val gender: String,
    val goal: String,
    val birthday: String,
    val height: Float,
    val weight: Float,
    val phone: String,
    val email: String,
    val consentPersonal: Boolean,
    val locationPersonal: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val isDeleted: Boolean,
    val notificationEnabled: Boolean
)

interface ProfileApiService {
    @GET("users/me")
    suspend fun fetchMyProfile(
    ): ApiResponse<FetchProfileResponse>
}