package com.example.diaviseo.network.auth.dto.res

data class GoogleLoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val newUser: Boolean
)
