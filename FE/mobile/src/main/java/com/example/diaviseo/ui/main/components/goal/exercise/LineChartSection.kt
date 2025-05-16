package com.example.diaviseo.ui.main.components.goal.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.network.exercise.dto.res.DailyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.MonthlyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.WeeklyExerciseStatsResponse
import com.example.diaviseo.ui.main.components.goal.meal.ChartPeriod
import com.example.diaviseo.ui.main.components.goal.meal.PeriodSelector
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.core.chart.line.LineChart
//import com.patrykandpatrick.vico.compose.component.marker.rememberDefaultMarker
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import java.time.LocalDate

@Composable
fun LineChartSection(
    dailyData: List<DailyExerciseStatsResponse.DailyExercise> = emptyList(),
    weeklyData: List<WeeklyExerciseStatsResponse.WeeklyExercise> = emptyList(),
    monthlyData: List<MonthlyExerciseStatsResponse.MonthlyExercise> = emptyList()
) {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.Day) }

    val goalViewModel : GoalViewModel = viewModel()
    val selectedDate by goalViewModel.selectedDate.collectAsState()

    LaunchedEffect(selectedDate) {
        selectedPeriod = ChartPeriod.Day
    }

    // 데이터 변환
    val lineChartEntries = when (selectedPeriod) {
        ChartPeriod.Day -> dailyData
            .asReversed()
            .map {
                LineChartSampleData.LineChartEntry(
                    dateLabel = LocalDate.parse(it.date).let { date ->
                        "${date.monthValue}/${date.dayOfMonth}"
                    }, // "2025-05-09" → "5/09" → "5/9"
                    kcal = it.totalCalories.toFloat()
                )
            }

        ChartPeriod.Week -> weeklyData
            .asReversed()
            .map {
                val month = it.startDate.substring(5, 7).toInt()
                val day = it.startDate.substring(8, 10).toInt()
                val weekOfMonth = getWeekOfMonth(it.startDate)
                LineChartSampleData.LineChartEntry(
                    dateLabel = "${month}월${weekOfMonth}주",
                    kcal = it.avgDailyCalories.toFloat()
                )
            }

        ChartPeriod.Month -> monthlyData
            .asReversed()
            .map {
                val month = it.yearMonth.substring(5, 7).toInt()
                LineChartSampleData.LineChartEntry(
                    dateLabel = "${month}월",
                    kcal = it.avgDailyCalories.toFloat()
                )
            }
    }

    val entries = lineChartEntries.mapIndexed { index, item -> entryOf(index.toFloat(), item.kcal) }
    val modelProducer = ChartEntryModelProducer(entries)

    val lineSpec = remember {
        LineChart.LineSpec(
            lineColor = DiaViseoColors.Main1.toArgb(), // 라인 색상 설정 [3]
        // Vico에서 곡선 설정을 위한 속성 확인 필요 (예: cubicStrength, lineStyle 등)
        // 예시: 라인 배경 그라데이션 설정 [1]
            lineBackgroundShader = DynamicShaders.fromBrush(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        DiaViseoColors.Main1.copy(alpha = 0.4f),
                        DiaViseoColors.Main1.copy(alpha = 0f)
                    )
                )
            )
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("그 동안 소모한 칼로리는?", style = semibold16, color = DiaViseoColors.Basic)
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            PeriodSelector(selected = selectedPeriod) { selectedPeriod = it }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Chart(
                chart = lineChart(lines = listOf(lineSpec)),
                chartModelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { value, _ ->
                        lineChartEntries.getOrNull(value.toInt())?.dateLabel ?: ""
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

fun getWeekOfMonth(dateString: String): Int {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = java.time.LocalDate.parse(dateString, formatter)
    val firstDayOfMonth = date.withDayOfMonth(1)
    val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7
    return ((date.dayOfMonth + dayOfWeekOffset - 1) / 7) + 1
}