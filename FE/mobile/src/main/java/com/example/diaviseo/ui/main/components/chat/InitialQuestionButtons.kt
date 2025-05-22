package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    topic: ChatTopic?,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val buttonLabels = when (topic) {
        ChatTopic.DIET -> listOf(
            "맘모스빵 영양성분은?",
            "돌솥비빔밥 나트륨은?",
            "양념게장 포화지방산은 얼마야?",
            "20대 남자의 평균 칼로리 섭취량은 얼마인가요?",
            "10대 여성의 하루 평균 단백질 섭취량을 알려주세요.",
            "30대 권장 나트륨 섭취량은?",
            "65세 이상 권장 칼로리는?",
            "직접 입력할게요"
        )
        ChatTopic.EXERCISE -> listOf(
            "운동 루틴 추천해줘",
            "오늘 운동 피드백 줘",
            "직접 입력할게요"
        )
        else -> emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(DiaViseoColors.Callout, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "아래 중 하나를 선택해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = DiaViseoColors.Basic,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        buttonLabels.forEach { label ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClick(label) },
                color = Color.White,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = DiaViseoColors.Basic,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DiaViseoColors.Main1
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
        InitialQuestionButtons(topic = ChatTopic.DIET, onClick = { })
    }
}