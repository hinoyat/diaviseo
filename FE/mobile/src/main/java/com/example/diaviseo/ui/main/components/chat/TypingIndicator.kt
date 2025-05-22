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
    val text = "디아비서가 입력 중입니다..."
    val waveTransition = rememberInfiniteTransition(label = "wave")

    // 조절 가능한 파라미터들
    val waveHeight = -3f // 파도 높이 (y축 offset)
    val waveSpeed = 1000 // 한 사이클 지속 시간 (ms)
    val waveDelayPerChar = 100 // 글자 간 딜레이 (ms)
    val alphaMin = 0.4f // 최소 알파 값
    val alphaMax = 1f   // 최대 알파 값

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        text.forEachIndexed { index, char ->
            val offsetY by waveTransition.animateFloat(
                initialValue = 0f,
                targetValue = waveHeight,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = waveSpeed,
                        delayMillis = index * waveDelayPerChar,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "charOffset-$index"
            )

            val alpha by waveTransition.animateFloat(
                initialValue = alphaMin,
                targetValue = alphaMax,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = waveSpeed,
                        delayMillis = index * waveDelayPerChar,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "charAlpha-$index"
            )

            Text(
                text = char.toString(),
                fontSize = 13.sp,
                color = DiaViseoColors.Placeholder.copy(alpha = alpha),
                modifier = Modifier.offset(y = offsetY.dp)
            )
        }
    }
}
