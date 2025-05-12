package com.example.diaviseo.network.meal.dto.req

import com.example.diaviseo.network.food.dto.req.MealTimeRequest

data class PostDietRequest(
    val mealDate: String, // 예: "2025-05-07"
    val isMeal: Boolean,
    val mealTimes: List<MealTimeRequest>
)