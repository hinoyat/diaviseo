package com.example.diaviseo.model.diet

import com.example.diaviseo.network.food.dto.req.FoodWithQuantityRequest

data class FoodWithQuantity(
    val foodId: Int,
    val foodName: String,
    val calorie: Int,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val sweet: Double,
    val quantity: Int
)

fun FoodWithQuantity.toRequest(): FoodWithQuantityRequest {
    return FoodWithQuantityRequest(
        foodId = this.foodId,
        quantity = this.quantity
    )
}