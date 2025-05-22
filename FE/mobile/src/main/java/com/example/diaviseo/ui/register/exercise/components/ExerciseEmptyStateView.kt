package com.example.diaviseo.ui.register.exercise.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium18
import com.example.diaviseo.ui.theme.regular14

enum class EmptyExerciseType {
    RECENT, FAVORITE
}

data class EmptyStateContent(
    val imageRes: Int,
    val text1: String,
    val text2: String,
    val textColor: Color
)

@Composable
fun ExerciseEmptyStateView(type: EmptyExerciseType) {
    val content = when (type) {
        EmptyExerciseType.RECENT -> EmptyStateContent(
            imageRes = R.drawable.recent_empty,
            text1 = "꾸준히 하는 운동들이",
            text2 = "자동으로 기록될거예요!",
            textColor = DiaViseoColors.Placeholder
        )
        EmptyExerciseType.FAVORITE -> EmptyStateContent(
            imageRes = R.drawable.favorite_empty,
            text1 = "",
            text2 = "아직 기록한 운동이 없어요",
            textColor = DiaViseoColors.Placeholder
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(65.dp))

        Image(
            painter = painterResource(id = content.imageRes),
            contentDescription = "운동 비어있음 이미지",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))

        if (content.text1.isNotBlank()) {
            Text(
                text = content.text1,
                style = medium18,
                lineHeight = 25.sp,
                color = content.textColor
            )
        }
        Text(
            text = content.text2,
            style = medium18,
            color = content.textColor,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center
        )
    }
}
