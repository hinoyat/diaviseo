package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.clip
import com.example.diaviseo.ui.theme.*

data class NutrientInfo(
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val sugar: Float
)

@Composable
fun NutrientBar(
    nutrientInfo: NutrientInfo,
    modifier: Modifier = Modifier
) {
    val total = nutrientInfo.carbohydrate + nutrientInfo.protein + nutrientInfo.fat + nutrientInfo.sugar
    val percentages = listOf(
        if (total > 0) nutrientInfo.carbohydrate / total else 0f,
        if (total > 0) nutrientInfo.protein / total else 0f,
        if (total > 0) nutrientInfo.fat / total else 0f,
        if (total > 0) nutrientInfo.sugar / total else 0f,
    )

    val labels = listOf("탄수화물", "단백질", "지방", "당류")
    val grams = listOf(
        nutrientInfo.carbohydrate,
        nutrientInfo.protein,
        nutrientInfo.fat,
        nutrientInfo.sugar
    )

    val colors = listOf(
        Color(0xFFB39DDB), // 탄수화물
        Color(0xFF64B5F6), // 단백질
        Color(0xFF4DD0E1), // 지방
        Color(0xFFF48FB1)  // 당류
    )

    Column(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(colors[index])
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$label ${grams[index].toInt()}g",
                        style = regular11
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 하단 그래프
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(7.dp))
        ) {
            if (total <= 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFE0E0E0))
                )
                return
            }

            percentages.forEachIndexed { index, percent ->
                val percentValue = percent * 100

                Box(
                    modifier = Modifier
                        .weight(if (percent > 0f) percent else 0.0001f)
                        .fillMaxHeight()
                        .background(colors[index]),
                    contentAlignment = Alignment.Center
                ) {
                    if (percentValue > 4f) {
                        Text(
                            text = "${(percent * 100).toInt()}%",
                            color = Color.White,
                            style = semibold10

                        )
                    }
                }
            }
        }
    }
}
