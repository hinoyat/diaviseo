package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium12
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.semibold16
import com.example.diaviseo.ui.theme.semibold18

@Composable
fun MealChartSection() {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.Day) }
    var selectedEntry by remember { mutableStateOf<StakedBarChartData.Entry?>(null) }

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
                data = StakedBarChartData.sampleData(selectedPeriod),
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
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { selectedEntry = null }
                    .padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("섭취량 상세", style = semibold18)
                    Spacer(Modifier.height(12.dp))
                    Text("탄수화물: ${entry.carbs} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("단백질:   ${entry.protein} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("지방:     ${entry.fat} kcal", style = medium14)
                    Spacer(Modifier.height(6.dp))
                    Text("당류:     ${entry.sugar} kcal", style = medium14)
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
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
