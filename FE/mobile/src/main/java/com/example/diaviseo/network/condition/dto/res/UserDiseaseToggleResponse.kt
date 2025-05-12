package com.example.diaviseo.network.condition.dto.res

import com.google.gson.annotations.SerializedName

data class UserDiseaseToggleResponse(
    val diseaseId: Long,

    @SerializedName("isRegistered")
    val isRegistered: Boolean,

    val message: String
)
