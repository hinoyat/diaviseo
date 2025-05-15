package com.example.diaviseo.network.meal

import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import com.example.diaviseo.network.meal.dto.res.MealDailyResponse
import com.example.diaviseo.network.meal.dto.res.NutritionStatsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MealApiService {
    @GET("meals/daily-nutrition")
    suspend fun fetchDailyNutrition(
        @Query("date") date: String
    ): ApiResponse<DailyNutritionResponse>

    @Multipart
    @POST("meals")
    suspend fun postDiet(
        @Part("mealData") mealData: RequestBody,
        @Part images: MultipartBody.Part? = null
    ): ApiResponse<Unit>

    @GET("meals/statistics/nutrition")
    suspend fun fetchMealStatistic(
        @Query("periodType") periodType: String,
        @Query("endDate") endDate: String
    ): ApiResponse<NutritionStatsResponse>

    @GET("meals/date/{date}")
    suspend fun fetchMealDaily(
        @Path("date") date: String
    ): ApiResponse<MealDailyResponse>
}