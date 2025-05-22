package com.example.diaviseo.network.auth.dto.req

data class GoogleLoginRequest(
    val provider: String,
    val idToken: String
)
