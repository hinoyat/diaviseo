package com.example.diaviseo.ui.main.components.goal

data class StepBarEntry(
    val dateLabel: String,
    val stepCount: Int
)

object StepBarChartData {
    fun getSampleData(): List<StepBarEntry> {
        return listOf(
            StepBarEntry("월", 4212),
            StepBarEntry("화", 5618),
            StepBarEntry("수", 3890),
            StepBarEntry("목", 6783),
            StepBarEntry("금", 5220),
            StepBarEntry("토", 7810),
            StepBarEntry("일", 6032)
        )
    }
}
