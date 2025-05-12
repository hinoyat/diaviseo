package com.example.diaviseo.network.food.dto.req

enum class MealType {
    BREAKFAST, LUNCH, DINNER;

    companion object {
        fun fromKorean(korean: String): MealType {
            return when (korean) {
                "아침" -> BREAKFAST
                "점심" -> LUNCH
                "저녁" -> DINNER
                else -> throw IllegalArgumentException("지원하지 않는 식사 유형: $korean")
            }
        }
    }
}