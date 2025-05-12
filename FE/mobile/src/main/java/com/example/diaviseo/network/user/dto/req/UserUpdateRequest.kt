package com.example.diaviseo.network.user.dto.req

data class UserUpdateRequest(
    val nickname: String? = null,
    val phone: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val birthday: String? = null, // ISO 8601 형태로: "2000-01-01"
    val notificationEnabled: Boolean? = null,
    val goal: String? = null
)
