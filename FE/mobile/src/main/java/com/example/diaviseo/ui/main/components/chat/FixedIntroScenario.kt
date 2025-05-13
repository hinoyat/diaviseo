package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors

enum class ChatTopic {
    DIET, EXERCISE
}

@Composable
fun FixedIntroScenario(
    onSelectTopic: (ChatTopic) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 디아비서 인사
        Text(
            text = "안녕하세요! 디아비서 방문을 환영합니다😄\n먼저, 어떤 주제로 이야기하실지 정해볼까요?",
            fontSize = 16.sp,
            color = DiaViseoColors.Basic,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 주제 선택 카드
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharacterCard(
                label = "식단이",
                imageRes = R.drawable.charac_eat,
                onClick = { onSelectTopic(ChatTopic.DIET) }
            )
            CharacterCard(
                label = "운동이",
                imageRes = R.drawable.charac_exercise,
                onClick = { onSelectTopic(ChatTopic.EXERCISE) }
            )
        }
    }
}

@Composable
fun CharacterCard(
    label: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
            .background(
                color = DiaViseoColors.Callout,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DiaViseoColors.Basic
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCharacterCard() {
    CharacterCard(label = "식단이", imageRes = R.drawable.charac_eat, onClick = {})
}
