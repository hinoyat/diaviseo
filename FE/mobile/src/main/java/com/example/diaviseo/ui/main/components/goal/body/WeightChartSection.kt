package com.example.diaviseo.ui.main.components.goal.body

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.network.body.dto.res.MonthlyAverageBodyInfoResponse
import com.example.diaviseo.network.body.dto.res.OcrBodyResultResponse
import com.example.diaviseo.network.body.dto.res.WeeklyAverageBodyInfoResponse
import com.example.diaviseo.ui.main.components.goal.meal.ChartPeriod
import com.example.diaviseo.ui.main.components.goal.meal.PeriodSelector
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.* // 사용자 테마 (ChartPeriod, WeightChartSampleData 등)
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.MealViewModel
import com.example.diaviseo.viewmodel.goal.WeightViewModel

// Vico 1.13.0 경로
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.entry.ChartEntry
import java.text.DecimalFormat
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent // 마커 배경용 모양 컴포넌트
import com.patrykandpatrick.vico.core.component.shape.Shapes
import java.time.LocalDate

//import com.patrykandpatrick.vico.compose.chart.marker.rememberMarker

// 모달에 표시할 데이터 클래스
data class ModalChartData(
    val dateLabel: String,
    val weight: String,
    val muscleMass: String,
    val bodyFat: String
)

data class WeightMultiLineEntry(
    val weight: Float,         // 체중 (kg)
    val muscleMass: Float,     // 골격근량 (kg)
    val bodyFat: Float,        // 체지방량 (kg)
    val dateLabel: String      // x축 라벨
)

@Composable
fun WeightChartSection() {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.Day) }

    // 모달 표시 상태 및 데이터
    var showDataModal by remember { mutableStateOf(false) }
    var modalData by remember { mutableStateOf<ModalChartData?>(null) }

    val weightViewModel : WeightViewModel = viewModel()
    val goalViewModel : GoalViewModel = viewModel()

    val selectedDate by goalViewModel.selectedDate.collectAsState()

    val dayList by weightViewModel.dayList.collectAsState()
    val weekList by weightViewModel.weekList.collectAsState()
    val monthList by weightViewModel.monthList.collectAsState()
    LaunchedEffect(selectedDate) {
        weightViewModel.fetchAllLists(selectedDate.toString())
    }

    val chartData: List<WeightMultiLineEntry> = when (selectedPeriod) {
        ChartPeriod.Day -> convertDayDataToChart(dayList)
        ChartPeriod.Week -> convertWeekDataToChart(weekList, selectedDate)
        ChartPeriod.Month -> convertMonthDataToChart(monthList, selectedDate)
    }

    // 3개의 라인에 대한 ChartEntry 리스트 생성
    val entriesLine1 = chartData.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.weight)
    }
    val entriesLine2 = chartData.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.muscleMass)
    }
    val entriesLine3 = chartData.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.bodyFat)
    }

    // ChartEntryModelProducer를 생성할 때 초기 엔트리 리스트를 전달
    val modelProducer = remember(entriesLine1, entriesLine2, entriesLine3) { // entries가 변경되면 프로듀서도 재생성 (또는 setEntries 사용)
        ChartEntryModelProducer(entriesLine1, entriesLine2, entriesLine3)
    }

    // 데이터가 변경될 때마다 modelProducer에 새로운 데이터 시리즈 리스트를 전달
    // setEntries에 List<List<ChartEntry>> 또는 varargs ChartEntry 리스트를 전달
    LaunchedEffect(entriesLine1, entriesLine2, entriesLine3) {
        val listOfEntryLists: List<List<ChartEntry>> = listOf(entriesLine1, entriesLine2, entriesLine3)

//        modelProducer.setEntries(listOfEntryLists)
        modelProducer.setEntries(entriesLine1, entriesLine2, entriesLine3)
    }

    // 3개의 라인에 대한 LineSpec 정의
    val weightLineSpec =  remember {
        LineChart.LineSpec(
            lineColor = 0xFF7987FF.toInt()
        )
    }
    val muscleLineSpec = remember {
        LineChart.LineSpec(
            lineColor = 0xFFE697FF.toInt()
        )
    }
    val fatLineSpec = remember {
        LineChart.LineSpec(
            lineColor = 0xFFFFA5CB.toInt()
        )
    }

    val allLineSpecs = remember(weightLineSpec, muscleLineSpec, fatLineSpec) { // lineSpec들이 변경될 때만 리스트 재생성
        listOf(weightLineSpec, muscleLineSpec, fatLineSpec)
    }

    // 마커의 레이블 컴포넌트는 투명하게 만들거나 매우 작게 만들어 화면에 보이지 않도록 함
    val transparentMarkerLabel = remember { TextComponent.Builder().apply { color = Color.Transparent.toArgb() }.build() }

    // 마커의 labelFormatter를 사용하여 모달 데이터 설정 및 표시 트리거
    val modalTriggerFormatter = remember(chartData) {
        object : MarkerLabelFormatter {
            private val decimalFormat = DecimalFormat("#.#")
            override fun getLabel(markedEntries: List<Marker.EntryModel>, chartValues: ChartValues): CharSequence {
                val xIndex = markedEntries.firstOrNull()?.entry?.x?.toInt()
                if (xIndex != null && xIndex >= 0 && xIndex < chartData.size) {
                    val dataPoint = chartData[xIndex]
                    modalData = ModalChartData(
                        dateLabel = dataPoint.dateLabel,
                        weight = decimalFormat.format(dataPoint.weight),
                        muscleMass = decimalFormat.format(dataPoint.muscleMass),
                        bodyFat = decimalFormat.format(dataPoint.bodyFat)
                    )
                    showDataModal = true // 모달 표시!
                }
                return "" // 실제 마커 레이블은 보이지 않으므로 빈 문자열 반환
            }
        }
    }

    val chartMarker = markerComponent(
        label = transparentMarkerLabel, // 화면에 보이지 않는 레이블
//        labelFormatter = modalTriggerFormatter,
        // indicator와 guideline은 필요에 따라 설정 (또는 null로 하여 보이지 않게)
        indicator = ShapeComponent(shape = Shapes.pillShape, color = Color.Transparent.toArgb()),
        guideline = LineComponent(color = Color.Transparent.toArgb())
    )


    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "그 동안 체중 변화는?",
                style = semibold16,
                color = DiaViseoColors.Basic
            )
            PeriodSelector(selected = selectedPeriod) {
                selectedPeriod = it
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Chart(
            chart = lineChart(lines = allLineSpecs), // 정의한 3개의 LineSpec 리스트 전달
            chartModelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, chartValues ->
                    val index = value.toInt()
                    // X축 레이블은 multiLineData의 dateLabel을 사용 (모든 라인이 동일한 X축 레이블 공유 가정)
                    if (index >= 0 && index < chartData.size) {
                        chartData[index].dateLabel
                    } else {
                        ""
                    }
                }
            ),
             marker = chartMarker // 필요시 마커 설정 (Vico 1.13.0 방식)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeightLegendItem("체중(kg)", Color(0xFF7987FF))
            WeightLegendItem("골격근량(kg)", Color(0xFFE697FF))
            WeightLegendItem("체지방량(kg)", Color(0xFFFFA5CB))
        }
    }
}

@Composable
fun WeightLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .padding(end = 4.dp)
                .background(color = color)
        )
        Text(text = label, color = DiaViseoColors.Basic, style = medium12)
    }
}


fun convertDayDataToChart(data: List<OcrBodyResultResponse>): List<WeightMultiLineEntry> {
    return data.map {
        val date = LocalDate.parse(it.measurementDate)
        WeightMultiLineEntry(
            weight = it.weight.toFloat(),
            muscleMass = it.muscleMass.toFloat(),
            bodyFat = it.bodyFat.toFloat(),
            dateLabel = "${date.monthValue}/${date.dayOfMonth}"
        )
    }
}

fun convertWeekDataToChart(
    data: List<WeeklyAverageBodyInfoResponse>,
    selectedDate: LocalDate
): List<WeightMultiLineEntry> {
    return (0..6).map { i ->
        val weekDate = selectedDate.minusWeeks((6 - i).toLong())
        val weekLabel = getKoreanWeekLabel(weekDate)
        val item = data.getOrNull(i)
        WeightMultiLineEntry(
            weight = item?.avgWeight?.toFloat() ?: 0f,
            muscleMass = item?.avgMuscleMass?.toFloat() ?: 0f,
            bodyFat = item?.avgBodyFat?.toFloat() ?: 0f,
            dateLabel = weekLabel
        )
    }
}

fun convertMonthDataToChart(
    data: List<MonthlyAverageBodyInfoResponse>,
    selectedDate: LocalDate
): List<WeightMultiLineEntry> {
    return (0..6).map { i ->
        val month = selectedDate.minusMonths((6 - i).toLong())
        val label = "${month.monthValue}월"
        val item = data.getOrNull(i)
        WeightMultiLineEntry(
            weight = item?.avgWeight?.toFloat() ?: 0f,
            muscleMass = item?.avgMuscleMass?.toFloat() ?: 0f,
            bodyFat = item?.avgBodyFat?.toFloat() ?: 0f,
            dateLabel = label
        )
    }
}

fun getKoreanWeekLabel(date: LocalDate): String {
    val firstDay = date.withDayOfMonth(1)
    val offset = firstDay.dayOfWeek.value % 7
    val weekOfMonth = ((date.dayOfMonth + offset - 1) / 7) + 1
    return "${date.monthValue}월${weekOfMonth}주"
}