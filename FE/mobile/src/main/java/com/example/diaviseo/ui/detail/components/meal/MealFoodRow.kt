package com.example.diaviseo.ui.detail.components.meal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.semibold14

@Composable
fun MealFoodRow(
    name: String,
    quantityLabel: String,  // 예: "2개", "1인분"
    kcal: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = semibold14,
            color = DiaViseoColors.Basic
        )
        Text(
            text = "$quantityLabel, ${kcal} kcal",
            style = semibold14,
            color = DiaViseoColors.Basic
        )
    }
}