package com.example.diaviseo.network.foodset

import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.foodset.dto.req.FoodSetRegisterRequest
import com.example.diaviseo.network.foodset.dto.res.FoodSetResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodSetApiService{

    @GET("food-sets")
    suspend fun getFoodSets():
            ApiResponse<List<FoodSetResponse>>


    @POST("food-sets")
    suspend fun postFoodSet(
        @Body request: FoodSetRegisterRequest
    ): ApiResponse<FoodSetResponse>
}