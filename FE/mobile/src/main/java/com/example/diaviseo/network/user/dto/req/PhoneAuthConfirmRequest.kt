package com.example.diaviseo.network.user.dto.req

data class PhoneAuthConfirmRequest(
    val phone: String,
    val code: String
)
