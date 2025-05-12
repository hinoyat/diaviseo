package com.example.diaviseo.ui.detail.components.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold16
import com.example.diaviseo.ui.theme.medium16

@Composable
fun MealSkippedCard(
    title: String,          // 예: "점심"
    kcal: Int = 0,
    gradient: Brush,
    mealIconRes: Int,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x1A222222),
                spotColor = Color(0x1A222222)
            )
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 상단: 아이콘 + 식사명 + 칼로리
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = mealIconRes),
                contentDescription = "$title 아이콘",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$title ",
                style = bold16,
                color = DiaViseoColors.Basic
            )
            Text(
                text = "($kcal kcal)",
                style = bold16,
                color = DiaViseoColors.Main1
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp), // 세로 가운데 정렬도 하고 싶으면 높이 지정 필요
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "오늘은 패스했어요! 😉",
                style = medium16,
                color = DiaViseoColors.Basic
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        MainButton(
            text = "수정하기",
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}