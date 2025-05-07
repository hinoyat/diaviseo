package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold18
import com.example.diaviseo.ui.theme.medium12

@Composable
fun Banner(
    isToday: Boolean,
    type: GoalBannerType,
    onClick: () -> Unit
) {
    val text = when (type) {
        GoalBannerType.MEAL -> if (isToday) "오늘 식단\n상세 보러가기" else "이 날의 식단\n상세 보러가기"
        GoalBannerType.EXERCISE -> if (isToday) "오늘 운동\n상세 보러가기" else "이 날의 운동\n상세 보러가기"
    }

    val imageRes = when (type) {
        GoalBannerType.MEAL -> com.example.diaviseo.R.drawable.dash_meal
        GoalBannerType.EXERCISE -> com.example.diaviseo.R.drawable.dash_exercise
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "dash background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 1) 텍스트: 좌측 세로 중앙
        Text(
            text = text,
            style = bold18,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
        )

        // 2) 버튼: 하단 우측
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DiaViseoColors.Main1)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Click!",
                style = medium12,
                color = Color.White
            )
        }
    }
}

enum class GoalBannerType {
    MEAL,
    EXERCISE
}