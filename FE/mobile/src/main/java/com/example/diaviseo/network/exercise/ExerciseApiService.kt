package com.example.diaviseo.network.exercise

import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.exercise.dto.res.ExerciseRecordResponse
import com.example.diaviseo.network.exercise.dto.res.DayExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.req.HealthSyncExerciseRequest
import com.example.diaviseo.network.exercise.dto.res.HealthSyncExerciseListResponse
import com.example.diaviseo.network.exercise.dto.req.StepRecordRequest
import com.example.diaviseo.network.exercise.dto.res.StepRecordResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    // 운동 삭제
    @DELETE("exercises/{exerciseId}")
    suspend fun deleteExercise(
        @Path("exerciseId") exerciseId: Int
    ): ApiResponse<Unit>
}