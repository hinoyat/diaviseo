package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
                imageRes = R.drawable.chat_char_exercise,
                onClick = { onSelectTopic(ChatTopic.EXERCISE) }
            )
            CharacterCard(
                imageRes = R.drawable.chat_char_diet,
                onClick = { onSelectTopic(ChatTopic.DIET) }
            )
        }
    }
}

@Composable
fun CharacterCard(
    imageRes: Int,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(170.dp)
            .clickable { onClick() }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewCharacterCard() {
    CharacterCard(imageRes = R.drawable.chat_char_diet, onClick = {})
}
