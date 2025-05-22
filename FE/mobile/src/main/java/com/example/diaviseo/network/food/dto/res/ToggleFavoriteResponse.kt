package com.example.diaviseo.network.food.dto.res

data class ToggleFavoriteResponse(
    val foodId: Int,
    val isFavorite: Boolean,
    val message: String,
    val toggledAt: String
)
