package com.example.diaviseo.ui.detail.components.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.Image
import com.example.diaviseo.R

@Composable
fun MealEmptyCard(
    title: String, // 예: "아침"
    kcal: Int = 0,
    gradient: Brush,
    mealIconRes: String,
    onSkippedClick: () -> Unit,
    onWriteClick: () -> Unit
) {
    val mealIcon = when (mealIconRes) {
        "BREAKFAST" -> R.drawable.morning
        "LUNCH" -> R.drawable.lunch
        "DINNER" -> R.drawable.night
        else -> R.drawable.apple
    }

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
            .padding(16.dp)
    ) {
        // 상단: 아이콘, 타이틀, kcal
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = mealIcon),
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
            text = "아직 작성된 식단이 없어요! 🌑\n혹시 거르셨나요?",
            style = regular14,
            color = DiaViseoColors.Basic
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(
                        color = DiaViseoColors.Main1,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSkippedClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("네, 안 먹었어요.", color = Color.White, style = bold14)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(
                        color = Color(0xFFC5C5C5),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onWriteClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("아뇨, 작성할게요!", color = Color.White, style = bold14)
            }
        }
    }
}