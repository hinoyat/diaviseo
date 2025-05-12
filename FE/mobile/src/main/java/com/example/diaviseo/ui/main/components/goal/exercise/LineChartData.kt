package com.example.diaviseo.ui.main.components.goal.exercise

import com.example.diaviseo.ui.main.components.goal.meal.ChartPeriod


object LineChartSampleData {

    data class LineChartEntry(
        val dateLabel: String,
        val kcal: Float
    )

    fun getSampleData(period: ChartPeriod): List<LineChartEntry> {
        return when (period) {
            ChartPeriod.Day -> listOf(
                LineChartEntry("4/20", 20f),
                LineChartEntry("4/21", 35f),
                LineChartEntry("4/22", 28f),
                LineChartEntry("4/23", 40f),
                LineChartEntry("4/24", 50f),
                LineChartEntry("4/25", 38f),
                LineChartEntry("4/26", 45f),
            )
            ChartPeriod.Week -> listOf(
                LineChartEntry("1주", 160f),
                LineChartEntry("2주", 180f),
                LineChartEntry("3주", 140f),
                LineChartEntry("4주", 190f),
                LineChartEntry("5주", 200f),
                LineChartEntry("6주", 170f),
                LineChartEntry("7주", 180f),
            )
            ChartPeriod.Month -> listOf(
                LineChartEntry("1월", 600f),
                LineChartEntry("2월", 720f),
                LineChartEntry("3월", 640f),
                LineChartEntry("4월", 800f),
                LineChartEntry("5월", 750f),
                LineChartEntry("6월", 770f),
                LineChartEntry("7월", 790f),
            )
        }
    }
}