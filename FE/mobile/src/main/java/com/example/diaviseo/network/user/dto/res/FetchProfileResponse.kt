package com.example.diaviseo.network.user.dto.res

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
