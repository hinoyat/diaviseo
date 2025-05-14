package com.example.diaviseo.network.food.dto.res

data class RecentFoodResponse(
    val timestamp: String,
    val status: String,
    val message: String,
    val data: List<RecentFoodItemResponse>
)

data class RecentFoodItemResponse(
    val foodId: Long,
    val foodName: String,
    val calorie: Int,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val sweet: Double,
    val baseAmount: String, // 단위 포함된 문자열 (예: "300.00")
    val isFavorite: Boolean
)