package com.example.diaviseo.network

import retrofit2.http.GET
import retrofit2.http.Query

data class FoodItem(
    val id: String,
    val name: String,
    val foodId : Int
)

data class FoodSearchResponse(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: List<FoodItem>
)

interface DietApiService {
    // 음식 검색
    @GET("foods/search/name")
    suspend fun searchFoodByName(
        @Query("name") name: String
    ): FoodSearchResponse
}