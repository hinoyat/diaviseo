package com.example.diaviseo.ui.main.components.goal.body

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
    weight: Double?,
    muscleMass: Double?,
    fatMass: Double?
) {
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
            val refMuscle = 30.0
            val refFat    = 32.0

            WeightInfoRow(label = "체중", value = weight, maxValue = refWeight)
            Spacer(modifier = Modifier.height(10.dp))
            WeightInfoRow(label = "골격근량", value = muscleMass, maxValue = refMuscle)
            Spacer(modifier = Modifier.height(10.dp))
            WeightInfoRow(label = "체지방량", value = fatMass, maxValue = refFat)

            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(vertical = 32.dp)
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DiaViseoColors.Placeholder.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = DiaViseoColors.Placeholder
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "아직 측정된 체중 정보가 없어요!",
                            style = semibold16,
                            color = DiaViseoColors.Unimportant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "지금 바로 체중을 기록해보세요.",
                            style = regular14,
                            color = DiaViseoColors.Placeholder
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeightInfoRow(
    label: String,
    value: Double,
    maxValue: Double
) {
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
            modifier = Modifier.width(92.dp),
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

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = String.format("%.1f", value),
            color = DiaViseoColors.Basic,
            style = medium14
        )
    }
}
