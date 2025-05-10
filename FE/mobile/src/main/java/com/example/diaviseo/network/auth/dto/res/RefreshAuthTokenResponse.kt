package com.example.diaviseo.network.auth.dto.res

data class RefreshAuthTokenResponse(
    val accessToken: String,
    val refreshToken: String
)