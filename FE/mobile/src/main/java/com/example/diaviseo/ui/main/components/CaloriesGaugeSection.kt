package com.example.diaviseo.ui.main.components

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.diaviseo.R

@Composable
fun CaloriesGaugeSection(
    consumedCalorie: Int,
    remainingCalorie: Int,
    burnedCalorie: Int,
    extraBurned: Int,
    navController: NavHostController
) {
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
            .padding(16.dp)
    ) {
        Column {
            // 운동 게이지
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.charac_exercise), // <- 이미지 필요
                    contentDescription = "운동 캐릭터",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CalorieGaugeBar(
                    label = "앞으로 $extraBurned kcal 더!",
                    value = burnedCalorie,
                    max = 280,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF5583FF), Color(0xFFFF84BA)))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 식사 게이지
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.charac_eat), // <- 이미지 필요
                    contentDescription = "식사 캐릭터",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CalorieGaugeBar(
                    label = "$remainingCalorie kcal 더 먹을 수 있어요",
                    value = consumedCalorie,
                    max = 1500,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFFFE282), Color(0xFF43D9E0)))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = {
                navController.navigate("calorieDetail")
            }) {
                Text(text = "상세 보러가기 >", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CalorieGaugeBar(
    label: String,
    value: Int,
    max: Int,
    gradient: Brush
) {
    val percent = value.toFloat() / max
    val animatedProgress by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        label = "gauge"
    )

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF222222)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE6E6E6)) // 게이지 배경
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = gradient)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "${value} kcal",
            fontSize = 14.sp,
            color = Color(0xFF222222)
        )
    }
}

