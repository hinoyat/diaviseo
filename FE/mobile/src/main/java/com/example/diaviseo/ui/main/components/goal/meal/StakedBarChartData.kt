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

    fun sampleData(period: ChartPeriod): List<Entry> {
        return when (period) {
            ChartPeriod.Day -> listOf(
                Entry("4/1", 180, 60, 40, 30),
                Entry("4/2", 150, 70, 50, 20),
                Entry("4/3", 200, 50, 60, 40),
                Entry("4/4", 170, 55, 45, 25),
                Entry("4/5", 190, 65, 55, 35),
                Entry("4/6", 160, 60, 50, 30),
                Entry("오늘", 210, 70, 60, 25)
            )

            ChartPeriod.Week -> listOf(
                Entry("3월4주", 1100, 400, 300, 200),
                Entry("4월1주", 1180, 450, 320, 210),
                Entry("4월2주", 1230, 430, 310, 180),
                Entry("4월3주", 1290, 480, 350, 250),
                Entry("4월4주", 1350, 500, 340, 230),
                Entry("5월1주", 1400, 520, 360, 240),
                Entry("이번주", 1380, 510, 350, 220)
            )

            ChartPeriod.Month -> listOf(
                Entry("11월", 4700, 1800, 1200, 900),
                Entry("12월", 4950, 1900, 1250, 920),
                Entry("1월", 5100, 1950, 1300, 950),
                Entry("2월", 5300, 2050, 1350, 970),
                Entry("3월", 5600, 2150, 1400, 1000),
                Entry("4월", 5800, 2250, 1450, 1050),
                Entry("5월", 6000, 2300, 1500, 1100)
            )
        }
    }
}