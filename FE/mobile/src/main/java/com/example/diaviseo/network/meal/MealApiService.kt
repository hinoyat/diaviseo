package com.example.diaviseo.network.meal

import com.example.diaviseo.network.auth.dto.res.RefreshAuthTokenResponse
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import retrofit2.http.*

interface MealApiService {
    @GET("meals/daily-nutrition")
    suspend fun fetchDailyNutrition(
        @Query("date") date: String
    ): ApiResponse<DailyNutritionResponse>
}