package com.example.diaviseo.network.meal

import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.meal.dto.req.PostDietRequest
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import retrofit2.http.*

interface MealApiService {
    @GET("meals/daily-nutrition")
    suspend fun fetchDailyNutrition(
        @Query("date") date: String
    ): ApiResponse<DailyNutritionResponse>

    @POST("meals")
    suspend fun postDiet(
        @Body request: PostDietRequest
    ): ApiResponse<Unit>

}