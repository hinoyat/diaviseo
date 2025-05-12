package com.example.diaviseo.network.food.dto.res

data class FoodDetailResponse(
    val foodId: Int,
    val foodName: String,
    val calorie: Int,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val sweet: Double,
    val sodium: Double,
    val saturatedFat: Double,
    val transFat: Double,
    val cholesterol: Double,
    val isFavorite: Boolean,
    val baseAmount: String
    )