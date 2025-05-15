package com.example.diaviseo.network.exercise.dto.res

data class ExerciseFavoriteToggleResponse(
    val exerciseTypeId: Int,
    val exerciseNumber: Int,
    val message: String,
    val toggledAt: String,
    val favorite: Boolean
)
