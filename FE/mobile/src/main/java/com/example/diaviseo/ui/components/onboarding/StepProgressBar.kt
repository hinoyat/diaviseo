package com.example.diaviseo.ui.components.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.ui.theme.DiaViseoColors


@Composable
fun StepProgressBar(
    currentStep: Int, // 1 ~ 4 단계까지 있음
    totalSteps: Int = 4 // 기본값: 4단계
) {
    val progressRatio by animateFloatAsState(
        targetValue = currentStep / totalSteps.toFloat(),
        label = "progress"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(progressRatio)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(DiaViseoColors.Main1)
        )
        Box(
            modifier = Modifier
                .weight(1 - progressRatio)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(DiaViseoColors.Deactive)
        )
    }
}
