package com.example.diaviseo.ui.main.components.goal.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium12
import com.example.diaviseo.ui.theme.medium14

@Composable
fun ExerciseItemCard(item: ExerciseItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = Color(0xFFD3DBFF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(26.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = item.name,
                style = medium14,
                color = DiaViseoColors.Basic
            )
        }

        // 오른쪽: 칼로리 / 시간
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${item.kcal} kcal",
                style = medium12,
                color = DiaViseoColors.Basic
            )
            Text(
                text = " / ",
                style = medium12,
                color = DiaViseoColors.Basic
            )
            Text(
                text = "${item.min}분",
                style = medium12,
                color = DiaViseoColors.Basic
            )
        }
    }
}
