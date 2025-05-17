package com.example.diaviseo.ui.main.components.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*

@Composable
fun CaloriesGaugeSection(
    recommendedEat: Int,
    recommendedFit: Int,
    consumedCalorie: Int,
    remainingCalorie: Int,
    burnedCalorie: Int,
    extraBurned: Int,
    navController: NavHostController
) {
    val textEatMessage = if (remainingCalorie >= 0) {
        "$remainingCalorie kcal 더 먹을 수 있어요 🥄"
    } else {
        "오늘 ${-remainingCalorie} kcal 초과했어요 🥲"
    }

    val textFitMessage = if (extraBurned >= 0) {
        "앞으로 $extraBurned  kcal 더! 💪"
    } else {
        "오늘 ${-extraBurned} kcal 더 운동했어요! 👍"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000), // 15% black
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            // 🔥 운동 게이지 (왼 → 오)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.charac_exercise),
                    contentDescription = "운동 캐릭터",
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = textFitMessage,
                        style = medium15,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GradientGaugeBar(
                        progress = burnedCalorie,
                        max = recommendedFit,
                        gradient = Brush.horizontalGradient(
                            listOf(Color(0xFF5583FF), Color(0xFFFF84BA))
                        ),
                        textColor = Color.White,
                        reverse = false,
                        valueText = "$burnedCalorie kcal"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🥗 식사 게이지 (오 → 왼)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = textEatMessage,
                        style = medium15,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GradientGaugeBar(
                        progress = consumedCalorie,
                        max = recommendedEat,
                        gradient = Brush.horizontalGradient(
                            listOf(Color(0xFFFFE282), Color(0xFF43D9E0))
                        ),
                        textColor = Color(0xFF222222),
                        reverse = true,
                        valueText = "$consumedCalorie kcal"
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Image(
                    painter = painterResource(id = R.drawable.charac_eat),
                    contentDescription = "식사 캐릭터",
                    modifier = Modifier.size(52.dp)

                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                TextButton(onClick = {
                    navController.navigate("home_detail") {
                        launchSingleTop = true
                        restoreState   = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }) {
                    Text(
                            text = "상세 보러가기 >",
                            style = medium15,
                            color = Color(0xFF464646)
                        )
                }
            }
        }
    }
}

@Composable
fun GradientGaugeBar(
    progress: Int,
    max: Int,
    gradient: Brush,
    textColor: Color,
    reverse: Boolean,
    valueText: String
) {
    val percent = progress.toFloat() / max
    val animatedProgress by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        label = "gauge"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEAEAEA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animatedProgress)
                .align(if (reverse) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(RoundedCornerShape(20.dp))
                .background(gradient),
            contentAlignment = if (reverse) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            Text(
                text = valueText,
                maxLines = 1,
                style = medium14,
                color = textColor,
                modifier = Modifier.padding(end = 12.dp, start = 12.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
        )
    }
}