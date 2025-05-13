package com.example.diaviseo.network.body

import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import com.example.diaviseo.network.body.dto.res.BodyRegisterResponse
import com.example.diaviseo.network.body.dto.res.OcrBodyResultResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BodyApiService {
    @POST("bodies")
    suspend fun registerBodyData(
        @Body request: BodyRegisterRequest
    ): ApiResponse<BodyRegisterResponse>

    @Multipart
    @POST("bodies/ocr")
    suspend fun uploadBodyOcrImage(
        @Part image: MultipartBody.Part
    ): ApiResponse<OcrBodyResultResponse>
}