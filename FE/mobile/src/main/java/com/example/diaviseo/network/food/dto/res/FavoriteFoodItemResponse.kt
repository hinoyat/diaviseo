package com.example.diaviseo.network.food.dto.res

data class FavoriteFoodItemResponse(
    val foodId: Int,
    val foodName: String,
    val calorie: Int,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val sweet: Double,
    val createdAt: String
)
