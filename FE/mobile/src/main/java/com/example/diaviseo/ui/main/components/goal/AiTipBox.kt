package com.example.diaviseo.ui.main.components.goal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*
import kotlinx.coroutines.delay


@Composable
fun AiTipBox(
    message: String = "",
    onRequestFeedback: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        if (message != "") {
            // 피드백이 있는 경우 - 메시지와 캐릭터 + 재요청 버튼
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 메시지 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0x40000000)
                        )
                        .background(
                            color = DiaViseoColors.Callout,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    if (isLoading) {
                        WavingText(
                            text = "열심히 분석 중이에요 잠시만 기다려주세요!",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = message,
                            color = DiaViseoColors.Basic,
                            style = regular14,
                            lineHeight = 20.sp
                        )
                    }
                }

                // 캐릭터를 박스 오른쪽 위에 배치
                Image(
                    painter = painterResource(id = R.drawable.charac_main_nontext),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-60).dp) // 박스 밖 오른쪽 위에 배치
                )

                // 말풍선 형태의 다시받기 버튼 (캐릭터 왼쪽에)
                Button(
                    onClick = onRequestFeedback,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-60).dp, y = (-45).dp) // 캐릭터 왼쪽에 배치
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .height(32.dp)
                        .wrapContentWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "다시받기",
                        color = DiaViseoColors.Main1,
                        style = semibold12,
                        fontSize = 10.sp
                    )
                }
            }
        } else {
            // 피드백이 없는 경우 - 빈 박스와 피드백 받기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(
                        width = 2.dp,
                        color = DiaViseoColors.BorderGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI 피드백을 받아보세요!",
                        color = DiaViseoColors.Middle,
                        style = regular14
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRequestFeedback,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .height(40.dp)
                            .wrapContentWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "피드백 받기",
                            color = DiaViseoColors.Main1,
                            style = semibold14,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WavingText(
    text: String,
    modifier: Modifier = Modifier,
    waveAmplitude: Dp = 4.dp,
    waveDuration: Int = 600
) {
    Row(modifier = modifier) {
        text.forEachIndexed { index, char ->
            val animatedOffsetY = remember { Animatable(0f) }

            // 무한 반복 애니메이션 (시작 지연을 index별로 줘서 파도처럼)
            LaunchedEffect(Unit) {
                delay(index * 60L) // index별 지연 시간
                while (true) {
                    animatedOffsetY.animateTo(
                        targetValue = -waveAmplitude.value,
                        animationSpec = tween(durationMillis = waveDuration / 2, easing = LinearEasing)
                    )
                    animatedOffsetY.animateTo(
                        targetValue = waveAmplitude.value,
                        animationSpec = tween(durationMillis = waveDuration, easing = LinearEasing)
                    )
                    animatedOffsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = waveDuration / 2, easing = LinearEasing)
                    )
                }
            }

            Text(
                text = char.toString(),
                modifier = Modifier
                    .offset(y = animatedOffsetY.value.dp),
                color = DiaViseoColors.Basic,
                style = regular14,
                lineHeight = 20.sp
            )
        }
    }
}
