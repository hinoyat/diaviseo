package com.example.diaviseo.network.user.dto.req

data class SignUpWithDiaRequest(
    val name: String,
    val nickname: String,
    val gender: String,
    val goal: String,
    val birthday: String,
    val height: Float,
    val weight: Float,
    val phone: String,
    val email: String,
    val provider: String,
    val consentPersonal: Boolean,
    val locationPersonal: Boolean,
)
