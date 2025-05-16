package com.example.diaviseo.ui.main.components.goal.body

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium14

@SuppressLint("DefaultLocale")
@Composable
fun BMRMBISection(
    bmr: Double,   // 기초대사량
    bmi: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoBox("나의 기초대사량 :  ", "${bmr.toInt()} kcal")
        InfoBox("나의 BMI  :  ", String.format("%.1f", bmi))
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