package com.example.diaviseo.ui.main.components.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val dotCount = rememberInfiniteTransition(label = "dots").animateValue(
        initialValue = 1,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "typingDotAnim"
    )

    val dots = ".".repeat(dotCount.value)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "디아비서가 입력 중입니다$dots",
            fontSize = 13.sp,
            color = DiaViseoColors.Placeholder
        )
    }
}
