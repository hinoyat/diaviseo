package com.example.diaviseo.network.exercise

import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.exercise.dto.res.ExerciseRecordResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ExerciseApiService {

    // 운동 등록
    @POST("exercises")
    suspend fun registerExercise(
        @Body request: ExerciseRecordRequest
    ): ApiResponse<ExerciseRecordResponse>
}