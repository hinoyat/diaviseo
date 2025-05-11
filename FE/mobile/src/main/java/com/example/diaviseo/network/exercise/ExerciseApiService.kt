package com.example.diaviseo.network.exercise

import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.exercise.dto.res.ExerciseRecordResponse
import com.example.diaviseo.network.exercise.dto.req.HealthSyncExerciseRequest
import com.example.diaviseo.network.exercise.dto.res.HealthSyncExerciseListResponse
import com.example.diaviseo.network.exercise.dto.req.StepRecordRequest
import com.example.diaviseo.network.exercise.dto.res.StepRecordResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ExerciseApiService {

    // 운동 등록
    @POST("exercises")
    suspend fun registerExercise(
        @Body request: ExerciseRecordRequest
    ): ApiResponse<ExerciseRecordResponse>

    // 헬스 커넥트 리스트 등록
    @POST("exercises/list")
    suspend fun uploadHealthExercises(
        @Body request: List<HealthSyncExerciseRequest>
    ): HealthSyncExerciseListResponse

    // 걸음수 등록
    @POST("/api/steps")
    suspend fun uploadStepRecords(
        @Body records: List<StepRecordRequest>
    ): ApiResponse<List<StepRecordResponse>>

}