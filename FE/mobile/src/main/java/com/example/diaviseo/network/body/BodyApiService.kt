package com.example.diaviseo.network.body

import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import com.example.diaviseo.network.body.dto.res.BodyInfoResponse
import com.example.diaviseo.network.body.dto.res.BodyRegisterResponse
import com.example.diaviseo.network.body.dto.res.MonthlyAverageBodyInfoResponse
import com.example.diaviseo.network.body.dto.res.OcrBodyResultResponse
import com.example.diaviseo.network.body.dto.res.WeeklyAverageBodyInfoResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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

    @GET("bodies/weekly")
    suspend fun fetchDailyBodyStatistic(
        @Query("endDate") endDate: String
    ): ApiResponse<List<OcrBodyResultResponse>>

    @GET("bodies/weekly-avg")
    suspend fun fetchWeeklyBodyStatistic(
        @Query("date") date: String
    ): ApiResponse<List<WeeklyAverageBodyInfoResponse>>

    @GET("bodies/monthly-avg")
    suspend fun fetchMonthlyBodyStatistic(
        @Query("date") date: String
    ): ApiResponse<List<MonthlyAverageBodyInfoResponse>>

    //체성분 날짜 조회
    @GET("bodies/date")
    suspend fun loadBodyData(
        @Query("date") date: String
    ): ApiResponse<BodyInfoResponse>

    @GET("bodies")
    suspend fun fetchLatestBodyData(): ApiResponse<List<BodyInfoResponse>>

}