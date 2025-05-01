package com.example.diaviseo.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeightPredictionSection(calorieDifference: Int) {
    val (text, color) = when {
        calorieDifference > 10 -> "📢 오늘은 몸무게가 증가할 예정이에요" to Color(0xFFFF3434)
        calorieDifference < -10 -> "📢 오늘은 몸무게가 감소할 예정이에요" to Color(0xFF1673FF)
        else -> "📢 오늘은 몸무게가 유지될 예정이에요" to Color(0xFF464646)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp)
    ) {

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}
