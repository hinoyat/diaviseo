package com.example.diaviseo.ui.main.components.goal.body

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*

fun calculateBMR(weightKg: Float, heightCm: Float, age: Int, isMale: Boolean): Int {
    return if (isMale) {
        (66.47 + 13.75 * weightKg + 5.003 * heightCm - 6.755 * age).toInt()
    } else {
        (655.1 + 9.563 * weightKg + 1.850 * heightCm - 4.676 * age).toInt()
    }
}

fun calculateBMI(weightKg: Float, heightCm: Float): Float {
    val heightM = heightCm / 100f
    return (weightKg / (heightM * heightM) * 10).toInt() / 10f // 소수점 1자리 반올림
}

@Composable
fun BMRMBISection(
    weight: Float,
    heightCm: Float,
    age: Int,
    isMale: Boolean
) {
    val bmr = calculateBMR(weight, heightCm, age, isMale)
    val bmi = calculateBMI(weight, heightCm)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoBox("나의 기초대사량 :  ", "${bmr} kcal")
        InfoBox("나의 BMI  :  ", "$bmi")
    }
}

@Composable
fun InfoBox(title: String, value: String) {
    Row(
        modifier = Modifier
            .width(230.dp)
            .border(
                width = Dp.Hairline,
                color = DiaViseoColors.Placeholder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, color = DiaViseoColors.Basic, style = medium14)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = DiaViseoColors.Basic, fontSize = 16.sp)
    }
}