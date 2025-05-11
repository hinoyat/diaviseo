package com.example.diaviseo.network.food

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.network.food.dto.res.FoodItem
import retrofit2.http.Path

interface FoodApiService {
    // 음식 검색
    @GET("foods/search/name")
    suspend fun searchFoodByName(
        @Query("name") name: String
    ): ApiResponse<List<FoodItem>>

    // 음식 상세 조회
    @GET("foods/{foodId}")
    suspend fun getFoodDetail(
        @Path("foodId") foodId: Int
    ): ApiResponse<FoodDetailResponse>
}