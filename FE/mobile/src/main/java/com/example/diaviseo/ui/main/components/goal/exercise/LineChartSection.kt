package com.example.diaviseo.ui.main.components.goal.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.main.components.goal.meal.ChartPeriod
import com.example.diaviseo.ui.main.components.goal.meal.PeriodSelector
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.core.chart.line.LineChart
//import com.patrykandpatrick.vico.compose.component.marker.rememberDefaultMarker
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis

@Composable
fun LineChartSection() {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.Day) }

    val data = LineChartSampleData.getSampleData(selectedPeriod)
    val entries = data.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.kcal)
    }
    val modelProducer =  ChartEntryModelProducer(entries)
    val lineSpec = remember { // LineSpec 정의
        LineChart.LineSpec(
            lineColor = DiaViseoColors.Main1.toArgb(), // 라인 색상 설정 [3]
            // Vico에서 곡선 설정을 위한 속성 확인 필요 (예: cubicStrength, lineStyle 등)
            // 예시: 라인 배경 그라데이션 설정 [1]
//            lineBackgroundShader = DynamicShaders.fromBrush(
//                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
//                    colors = listOf(
//                        Color.Green.copy(alpha = 0.4f),
//                        Color.Green.copy(alpha = 0f)
//                    )
//                )
//            )
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "그 동안 소모한 칼로리는?",
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

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
//                .padding(horizontal = 16.dp)
        ) {
            Chart(
                chart = lineChart(lines = listOf(lineSpec)), // 기본 라인차트 구성
                chartModelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                startAxis = rememberStartAxis(),             // 시작 축 (Y축) 설정 [1]
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { value, chartValues ->
                        // 'value'는 Vico가 전달하는 X축의 Float 값 (주로 인덱스에 해당)
                        val index = value.toInt()

                        // 데이터 리스트 범위 내에 있는지 확인하고 해당 dateLabel 반환
                        if (index >= 0 && index < data.size) {
                            data[index].dateLabel // chartData 리스트에서 해당 인덱스의 dateLabel 반환 [10][2]
                        } else {
                            "" // 범위를 벗어나면 빈 문자열 반환
                        }
                    }
                    // , guideline = null // 필요에 따라 다른 파라미터 설정 [2]
                    // , itemPlacer = AxisItemPlacer.Horizontal.default(...) // 레이블 간격 등 조절 [2]
                ),           // 하단 축 (X축) 설정 [1]
//                bottomAxis = rememberAxis(valueFormatter = { value ->
//                    val index = value.toInt()
//                    data.xLabels.getOrNull(index) ?: ""
//                }),
//                marker = rememberMarker()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 범례
//        Text(
//            text = "• 소모 칼로리",
//            color = DiaViseoColors.Main1,
//            modifier = Modifier.padding(start = 16.dp)
//        )
    }
}
