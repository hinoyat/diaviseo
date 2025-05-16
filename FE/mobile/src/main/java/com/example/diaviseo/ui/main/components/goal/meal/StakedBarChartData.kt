package com.example.diaviseo.ui.main.components.goal.meal

object StakedBarChartData {
    data class Entry(
        val label: String,
        val carbs: Int,
        val protein: Int,
        val fat: Int,
        val sugar: Int
    ) {
        val total: Int
            get() = carbs + protein + fat + sugar
    }
}