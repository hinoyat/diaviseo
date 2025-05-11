package com.example.diaviseo.ui.detail.components.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.diaviseo.ui.theme.*

@Composable
fun MealSkippedCard(
    title: String,          // 예: "점심"
    kcal: Int = 0,
    gradient: Brush,
    mealIconRes: Int
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

        Text(
            text = "오늘은 패스했어요! 😶‍🌫️",
            style = regular14,
            color = DiaViseoColors.Basic
        )
    }
}