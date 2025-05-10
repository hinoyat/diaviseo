package com.example.diaviseo.network.exercise

import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.exercise.dto.res.ExerciseRecordResponse
import com.example.diaviseo.network.exercise.dto.res.DayExerciseStatsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExerciseApiService {
    // 운동 등록
    @POST("exercises")
    suspend fun registerExercise(
        @Body request: ExerciseRecordRequest
    ): ApiResponse<ExerciseRecordResponse>

    // 날짜로 해당날 운동 조회
    @GET("exercises/today")
    suspend fun fetchDailyExercise(
        @Query("date") date: String
    ): ApiResponse<DayExerciseStatsResponse>
}