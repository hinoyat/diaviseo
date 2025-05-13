package com.example.diaviseo.network.body

import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import com.example.diaviseo.network.body.dto.res.BodyRegisterResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BodyApiService {
    @POST("bodies")
    suspend fun registerBodyData(
        @Body request: BodyRegisterRequest
    ): ApiResponse<BodyRegisterResponse>
}