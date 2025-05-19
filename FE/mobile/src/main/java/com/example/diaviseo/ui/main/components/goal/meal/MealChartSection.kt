package com.example.diaviseo.ui.main.components.goal.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.network.meal.dto.res.NutritionStatsEntry
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.MealViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun MealChartSection() {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.Day) }
    var selectedEntry by remember { mutableStateOf<StakedBarChartData.Entry?>(null) }

    val mealViewModel : MealViewModel = viewModel()
    val goalViewModel : GoalViewModel = viewModel()

    val selectedDate by goalViewModel.selectedDate.collectAsState()

    LaunchedEffect(selectedPeriod) {
        mealViewModel.fetchMealStatistic(selectedPeriod.name.toUpperCase(), selectedDate.toString())
    }

    LaunchedEffect(selectedDate) {
        selectedPeriod = ChartPeriod.Day
    }

    val mealStatistic by mealViewModel.mealStatistic.collectAsState()

    val chartData = mealStatistic?.toChartEntries(selectedPeriod).orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "그 동안 섭취한 칼로리는?",
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                PeriodSelector(
                    selected = selectedPeriod,
                    onSelectedChange = { selectedPeriod = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 차트
            StakedBarChart(
                data = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp),
                onBarClick = { entry -> selectedEntry = entry }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 범례
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                LegendRow()
            }
        }

        // Overlay: Bar 클릭 시
        selectedEntry?.let { entry ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { selectedEntry = null }
                    .padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .shadow(
                            elevation = 6.dp,
                            spotColor = Color(0x26000000), // 15% black
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.White)
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("섭취량 상세", style = semibold18)
                    Spacer(Modifier.height(12.dp))
                    Text("탄수화물: ${entry.carbs * 4} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("단백질:   ${entry.protein * 4} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("지방:     ${entry.fat * 9} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("당류:     ${entry.sugar * 4} kcal", style = medium14)
                }
            }
        }
    }
}

enum class ChartPeriod(val label: String) {
    Day("일"),
    Week("주"),
    Month("월")
}

@Composable
fun PeriodSelector(
    selected: ChartPeriod,
    onSelectedChange: (ChartPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChartPeriod.values().forEach { period ->
            val isSelected = period == selected

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .background(
                        color = if (isSelected) DiaViseoColors.Main1 else DiaViseoColors.Placeholder,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectedChange(period) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period.label,
                    style = medium12,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LegendRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem("탄수화물", DiaViseoColors.Carbohydrate)
        LegendItem("단백질", DiaViseoColors.Protein)
        LegendItem("지방", DiaViseoColors.Fat)
        LegendItem("당류", DiaViseoColors.Sugar)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .padding(end = 4.dp)
                .background(color = color)
        )
        Text(
            text = label,
            color = DiaViseoColors.Basic,
            style = medium12,
        )
    }
}

fun List<NutritionStatsEntry>.toChartEntries(period: ChartPeriod): List<StakedBarChartData.Entry> {
    return this.map {
        val date = LocalDate.parse(it.label)
        val label = when (period) {
            ChartPeriod.Day -> "${date.monthValue}/${date.dayOfMonth}" // 예: 4/1
            ChartPeriod.Week -> {
                val weekOfMonth = date.get(WeekFields.of(Locale.KOREA).weekOfMonth())
                "${date.monthValue}월${weekOfMonth}주" // 예: 5월1주
            }
            ChartPeriod.Month -> "${date.monthValue}월" // 예: 5월
        }

        StakedBarChartData.Entry(
            label = label,
            carbs = it.carbs,
            protein = it.protein,
            fat = it.fat,
            sugar = it.sugar
        )
    }
}