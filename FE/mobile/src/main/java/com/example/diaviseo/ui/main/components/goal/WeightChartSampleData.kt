package com.example.diaviseo.ui.main.components.goal

data class WeightMultiLineEntry(
    val weight: Float,         // 체중 (kg)
    val muscleMass: Float,     // 골격근량 (kg)
    val bodyFat: Float,        // 체지방량 (kg)
    val dateLabel: String      // x축 라벨
)

object WeightChartSampleData {
    fun getSampleData(period: ChartPeriod): List<WeightMultiLineEntry> {
        return when (period) {
            ChartPeriod.Day -> listOf(
                WeightMultiLineEntry(59.1f, 19.3f, 22.1f, "월"),
                WeightMultiLineEntry(59.2f, 19.4f, 22.0f, "화"),
                WeightMultiLineEntry(59.3f, 19.5f, 21.8f, "수"),
                WeightMultiLineEntry(59.4f, 19.5f, 21.7f, "목"),
                WeightMultiLineEntry(59.5f, 19.6f, 21.6f, "금"),
                WeightMultiLineEntry(59.7f, 19.7f, 21.5f, "토"),
                WeightMultiLineEntry(59.9f, 19.8f, 21.4f, "일")
            )

            ChartPeriod.Week -> listOf(
                WeightMultiLineEntry(58.5f, 19.0f, 22.3f, "1주"),
                WeightMultiLineEntry(59.0f, 19.2f, 22.0f, "2주"),
                WeightMultiLineEntry(59.4f, 19.3f, 21.9f, "3주"),
                WeightMultiLineEntry(59.8f, 19.4f, 21.7f, "4주"),
                WeightMultiLineEntry(60.1f, 19.6f, 21.5f, "5주"),
                WeightMultiLineEntry(60.3f, 19.7f, 21.4f, "6주"),
                WeightMultiLineEntry(60.5f, 19.8f, 21.2f, "7주")
            )

            ChartPeriod.Month -> listOf(
                WeightMultiLineEntry(58.8f, 18.9f, 22.5f, "1월"),
                WeightMultiLineEntry(59.5f, 19.2f, 22.2f, "2월"),
                WeightMultiLineEntry(59.9f, 19.3f, 21.9f, "3월"),
                WeightMultiLineEntry(60.2f, 19.5f, 21.7f, "4월"),
                WeightMultiLineEntry(60.4f, 19.6f, 21.4f, "5월"),
                WeightMultiLineEntry(60.5f, 19.7f, 21.3f, "6월"),
                WeightMultiLineEntry(60.6f, 19.8f, 21.2f, "7월")
            )
        }
    }
}
