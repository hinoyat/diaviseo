package com.example.diaviseo.network.condition

import com.example.diaviseo.network.common.dto.ApiResponse
import com.example.diaviseo.network.condition.dto.res.DiseaseResponse
import com.example.diaviseo.network.condition.dto.res.FoodAllergyResponse
import com.example.diaviseo.network.condition.dto.res.UserAllergyResponse
import com.example.diaviseo.network.condition.dto.res.UserAllergyToggleResponse
import com.example.diaviseo.network.condition.dto.res.UserDiseaseResponse
import com.example.diaviseo.network.condition.dto.res.UserDiseaseToggleResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConditionApiService {

    // 기준 알러지 목록 조회
    @GET("bodies/allergies")
    suspend fun getAllAllergies(): ApiResponse<List<FoodAllergyResponse>>

    // 유저 알러지 목록 조회
    @GET("bodies/allergies/my")
    suspend fun getUserAllergies(): ApiResponse<List<UserAllergyResponse>>

    // 알러지 토글 등록 / 해제
    @POST("bodies/allergies/{allergyId}/toggle")
    suspend fun toggleAllergy(
        @Path("allergyId") allergyId: Long
    ): ApiResponse<UserAllergyToggleResponse>

    @GET("bodies/diseases")
    suspend fun getAllDiseases(): ApiResponse<List<DiseaseResponse>>

    @GET("bodies/diseases/my")
    suspend fun getUserDiseases(): ApiResponse<List<UserDiseaseResponse>>

    @POST("bodies/diseases/{diseaseId}/toggle")
    suspend fun toggleDisease(
        @Path("diseaseId") diseaseId: Long
    ): ApiResponse<UserDiseaseToggleResponse>
}
