package com.example.diaviseo.network.condition.dto.res

import com.google.gson.annotations.SerializedName

data class UserAllergyResponse(
    val allergyId: Long,
    val allergyName: String,

    @SerializedName("isRegistered")
    val isRegistered: Boolean
)
