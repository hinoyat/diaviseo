package com.example.diaviseo.ui.main.components.goal.body

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.WeightViewModel

@Composable
fun WeightOverviewSection(
    isToday: Boolean,
    isMale: Boolean,
    weight: Double?,
    muscleMass: Double?,
    fatMass: Double?
) {
    val weigthViewModel : WeightViewModel = viewModel()
    val goalViewModel : GoalViewModel = viewModel()

    val selectedDate by goalViewModel.selectedDate.collectAsState()

    val title = if (isToday) "오늘의 체중 정보는?" else "이 날의 체중 정보는?"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = DiaViseoColors.Basic,
            style = semibold16
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (weight != null && muscleMass != null && fatMass != null) {
            // Float 리터럴(f) 대신 Double 리터럴(.0) 사용
            val refWeight = weight + 20.0
            val refMuscle = if (isMale) 40.0 else 30.0
            val refFat    = if (isMale) 25.0 else 32.0

            WeightInfoRow(label = "체중", value = weight, maxValue = refWeight)
            Spacer(modifier = Modifier.height(10.dp))
            WeightInfoRow(label = "골격근량", value = muscleMass, maxValue = refMuscle)
            Spacer(modifier = Modifier.height(10.dp))
            WeightInfoRow(label = "체지방량", value = fatMass, maxValue = refFat)

            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Text(
                text = "입력된 체중 정보가 없어요",
                color = DiaViseoColors.Placeholder,
                style = medium14,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeightInfoRow(
    label: String,
    value: Double,
    maxValue: Double // 기준 상한값 (ex: 인바디 표준값)
) {
    // Double 결과를 Float으로 변환한 뒤 coerceIn
    val ratio = (value / maxValue)
        .toFloat()
        .coerceIn(0f, 1.2f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label (kg)",
            color = DiaViseoColors.Basic,
            modifier = Modifier.width(78.dp),
            style = regular14
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .height(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(14.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF5A3FFF), Color(0xFF1ED6FF))
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        Text(
            text = String.format("%.1f", value),
            color = DiaViseoColors.Basic,
            style = medium14
        )
    }
}
