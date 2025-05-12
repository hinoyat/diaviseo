package com.example.diaviseo.network.foodset.dto.req

data class FoodSetRegisterRequest(
    val name: String,
    val foods: List<FoodIdWithQuantity>
)

data class FoodIdWithQuantity(
    val foodId: Int,
    val quantity: Double
)