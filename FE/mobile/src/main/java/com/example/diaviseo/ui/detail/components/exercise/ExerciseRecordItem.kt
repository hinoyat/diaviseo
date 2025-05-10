package com.example.diaviseo.ui.detail.components.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold20
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.regular14
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseRecordItem(
    title: String,
    kcal: Int,
    time: Int,
    exerciseDate: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val parsed = LocalDateTime.parse(exerciseDate)
    val exerciseTime = parsed.format(DateTimeFormatter.ofPattern("HH:mm"))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x3B222222),
                spotColor = Color(0x3A222222)
            )
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD3DBFF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"),
                    contentDescription = "운동 아이콘",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = medium14, color = DiaViseoColors.Basic)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "$kcal kcal", style = bold20, color = DiaViseoColors.Unimportant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "$time 분  |  $exerciseTime", style = regular14, color = DiaViseoColors.Unimportant)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "수정",
                    style = medium16,
                    color = DiaViseoColors.Basic,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable(onClick = onEditClick)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "삭제",
                    style = medium16,
                    color = DiaViseoColors.Basic,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable(onClick = onDeleteClick)
                )
            }
        }
    }
}
