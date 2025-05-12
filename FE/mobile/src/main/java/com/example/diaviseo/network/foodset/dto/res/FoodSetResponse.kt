package com.example.diaviseo.network.foodset.dto.res

data class FoodSetListResponse(
    val status: String,
    val message: String,
    val data: List<FoodSetResponse>
)

data class FoodSetResponse(
    val foodSetId: Int,
    val name: String,
    val foods: List<FoodInSetResponse>,
    val totalCalories: Int
)

data class FoodInSetResponse(
    val foodId: Int,
    val foodName: String,
    val quantity: Double,
    val calorie: Int,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val sweet: Double
)