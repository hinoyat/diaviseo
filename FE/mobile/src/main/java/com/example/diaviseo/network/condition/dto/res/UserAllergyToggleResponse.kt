package com.example.diaviseo.network.condition.dto.res

import com.google.gson.annotations.SerializedName

data class UserAllergyToggleResponse(
    val allergyId: Long,

    @SerializedName("isRegistered")
    val isRegistered: Boolean,

    val message: String
)
