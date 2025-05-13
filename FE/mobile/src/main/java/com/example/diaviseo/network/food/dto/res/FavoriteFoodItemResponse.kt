package com.example.diaviseo.network.food.dto.res

data class FavoriteFoodItemResponse(
    val foodId: Int,
    val foodName: String,
    val calorie: Int,
    val createdAt: String
)
