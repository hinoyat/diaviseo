package com.example.diaviseo.network.condition.dto.res

import com.google.gson.annotations.SerializedName

data class UserDiseaseResponse(
    val diseaseId: Long,
    val diseaseName: String,

    @SerializedName("isRegistered")
    val isRegistered: Boolean
)
