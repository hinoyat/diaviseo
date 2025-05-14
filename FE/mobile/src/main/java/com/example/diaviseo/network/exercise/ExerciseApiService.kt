package com.example.diaviseo.network.exercise

import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.exercise.dto.req.ExercisePutRecordRequest
import com.example.diaviseo.network.exercise.dto.res.ExerciseRecordResponse
import com.example.diaviseo.network.exercise.dto.res.DayExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.req.HealthSyncExerciseRequest
import com.example.diaviseo.network.exercise.dto.res.HealthSyncExerciseListResponse
import com.example.diaviseo.network.exercise.dto.req.StepRecordRequest
import com.example.diaviseo.network.exercise.dto.res.DailyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.MonthlyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.StepRecordResponse
import com.example.diaviseo.network.exercise.dto.res.StepWeeklyResponse
import com.example.diaviseo.network.exercise.dto.res.WeeklyExerciseStatsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    // 일주일 걸음 수 조회
    @GET("exercises/step-week")
    suspend fun fetchStepWeekly(
    ): ApiResponse<StepWeeklyResponse>

    // 운동 삭제
    @DELETE("exercises/{exerciseId}")
    suspend fun deleteExercise(
        @Path("exerciseId") exerciseId: Int
    ): ApiResponse<Unit>

    // 운동 수정
    @PUT("exercises/{exerciseId}")
    suspend fun putExercise(
        @Path("exerciseId") exerciseId: Int,
        @Body request: ExercisePutRecordRequest
    ): ApiResponse<ExerciseRecordResponse>

    // 날짜로 7일 운동 기록 조회
    @GET("exercises/daily")
    suspend fun getDailyExerciseStats(
        @Query("date") date: String
    ): ApiResponse<DailyExerciseStatsResponse>

    // 날짜로 7주 운동 평균 기록 조회
    @GET("exercises/weekly")
    suspend fun getWeeklyExerciseStats(
        @Query("date") date: String
    ): ApiResponse<WeeklyExerciseStatsResponse>

    // 날짜로 7달 운동 평균 기록 조회
    @GET("exercises/monthly")
    suspend fun getMonthlyExerciseStats(
        @Query("date") date: String
    ): ApiResponse<MonthlyExerciseStatsResponse>
}