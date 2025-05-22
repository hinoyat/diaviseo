package com.example.diaviseo.network.food

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.food.dto.res.FavoriteFoodItemResponse
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse
import com.example.diaviseo.network.food.dto.res.ToggleFavoriteResponse
import retrofit2.http.POST
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

    // 최근 먹은 음식 목록 조회
    @GET("meals/recent-foods")
    suspend fun getRecentFoods():
            ApiResponse<List<RecentFoodItemResponse>>
    
    // 즐겨찾기 토글
    @POST("foods/favorites/{foodId}")
    suspend fun toggleFavoriteFood(
        @Path("foodId") foodId: Int
    ): ApiResponse<ToggleFavoriteResponse>

    // 즐겨찾기 목록 조회
    @GET("foods/favorites")
    suspend fun getFavoriteFoods(): ApiResponse<List<FavoriteFoodItemResponse>>

}