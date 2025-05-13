package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun InitialQuestionButtons(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val buttonLabels = listOf(
        "오늘 내 점심 평가해줘",
        "오늘 점심 평가해줘",
        "직접 입력할게요"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(DiaViseoColors.Callout, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "아래 중 하나를 선택해보세요",
            fontSize = 16.sp,
            color = DiaViseoColors.Basic,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        buttonLabels.forEach { label ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, DiaViseoColors.Main1, RoundedCornerShape(16.dp))
                    .clickable { onClick(label) },
                color = Color(0xFFF7F9FC),
                tonalElevation = 2.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👉", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = DiaViseoColors.Basic
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInitialQuestionButtons() {
    MaterialTheme {
        InitialQuestionButtons(onClick = { })
    }
}
