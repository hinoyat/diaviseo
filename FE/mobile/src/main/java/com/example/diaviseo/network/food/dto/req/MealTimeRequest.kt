package com.example.diaviseo.network.food.dto.req

data class MealTimeRequest(
    val mealType: MealType, // 예: "BREAKFAST", "LUNCH", "DINNER"
    val eatingTime: String, // 예: "12:30:00"
    val foods: List<FoodWithQuantityRequest>
)