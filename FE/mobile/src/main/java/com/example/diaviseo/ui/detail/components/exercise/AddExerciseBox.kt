package com.example.diaviseo.ui.detail.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*

@Composable
fun AddExerciseBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x3A222222),
                spotColor = Color(0x3A222222)
            )
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🏋️‍♂️ 운동 더 추가하기",
            style = medium16,
            color = DiaViseoColors.Basic
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddExerciseBoxPreview() {
    AddExerciseBox(
        onClick= {}
    )
}