package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent // 마커 레이블용 텍스트 컴포넌트
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent // 마커 배경용 모양 컴포넌트
import com.patrykandpatrick.vico.core.component.shape.Shapes // 미리 정의된 모양 사용
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions // 패딩 설정용


@Composable
fun StepBarChart(modifier: Modifier = Modifier) {
    val data = StepBarChartData.getSampleData()

    // Bar entries 준비
    val entries = data.mapIndexed { index, entry ->
        entryOf(index.toInt(), entry.stepCount)
    }

    val chartModelProducer = remember { ChartEntryModelProducer(entries) }

    val customColumns = remember { // remember를 사용하여 불필요한 재생성 방지
        listOf(
            LineComponent(
                color = DiaViseoColors.Main2.toArgb(),       // 막대의 색상을 원하는 색으로 변경합니다. [2]
                thicknessDp = 20f,         // 막대의 두께(너비)를 원하는 크기로 변경합니다. [2]
            )
        )
    }

    val markerLabel = remember {
        TextComponent.Builder().apply {
            color = Color.White.toArgb() // 텍스트 색상
            // 배경 설정
            background = ShapeComponent(
                shape = Shapes.roundedCornerShape(allPercent = 25), // 모서리가 둥근 사각형
                color = DiaViseoColors.Callout.toArgb() // 배경색
            )
            // 패딩 설정 (core 컴포넌트는 보통 Float 값을 직접 받음, dp 값 변환 필요 시 toPx() 등 사용)
//            padding = MutableDimensions(horizontal = 8f, vertical = 4f) // 예시: 8px, 4px
            // 또는 dp 값을 변환: val density = LocalDensity.current.density
            // padding = MutableDimensions(horizontal = (8.dp * density).value, vertical = (4.dp * density).value)
        }.build()
    }

    // 2. 마커 인디케이터 (데이터 포인트를 가리키는 작은 점) - 기본값 설정
    val markerIndicator = remember {
        ShapeComponent(
            shape = Shapes.pillShape, // 원형 인디케이터 (크기는 마커 로직에 의해 조절됨)
            color = DiaViseoColors.Callout.toArgb()
            // 원하는 경우 크기나 스트로크 등을 명시적으로 설정할 수 있으나,
            // 보통 마커 시스템이 크기를 적절히 조절합니다.
        )
    }

    // 3. 마커 가이드라인 (데이터 포인트에서 축으로 이어지는 선) - 기본값 설정
    val markerGuideline = remember {
        LineComponent(
            color = Color.Transparent.toArgb(),
        )
    }

    val customTooltipMarker = markerComponent(
        label = markerLabel,
        indicator = markerIndicator,
        guideline = markerGuideline
    )

    Chart(
        chart = columnChart(columns = customColumns),
        chartModelProducer = chartModelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        startAxis = rememberStartAxis(
            valueFormatter = { value, chartValues ->
                // value는 Float 타입이므로, toInt()를 사용하여 정수로 변환 후 문자열로 만듭니다.
                value.toInt().toString()
            }
            // 필요한 경우 다른 Y축 설정 추가 (예: 레이블 색상, 가이드라인 등)
            // label = rememberTextComponent(color = YourColor),
//             maxLabelCount = 5 // Y축 레이블 개수 제한 (선택 사항)
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                data.getOrNull(value.toInt())?.dateLabel ?: ""
            },
        ),
        marker = customTooltipMarker
    )
}