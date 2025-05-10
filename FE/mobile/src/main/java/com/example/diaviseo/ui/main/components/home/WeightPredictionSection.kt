package com.example.diaviseo.ui.main.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import com.example.diaviseo.ui.theme.*

@Composable
fun WeightPredictionSection(calorieDifference: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val (textPart, highlight) = when {
                calorieDifference > 50 -> "증가" to Color(0xFFFF3434)
                calorieDifference < -50 -> "감소" to Color(0xFF1673FF)
                else -> "유지" to Color(0xFF464646)
            }

            Text(
                buildAnnotatedString {
                    append("📢   오늘은 몸무게가 ")
                    withStyle(SpanStyle(color = highlight, fontFamily = Wanted, fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(textPart)
                    }
                    append("될 예정이에요")
                },
                style = medium16,
                textAlign = TextAlign.Center,
                color = Color(0xFF222222)
            )
        }
    }
}
