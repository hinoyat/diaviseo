package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.R

@Composable
fun ChatMessageBubble(
    message: String,
    isUser: Boolean,
    characterImageRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // 챗봇 캐릭터 이미지 (왼쪽)
        if (!isUser && characterImageRes != null) {
            Image(
                painter = painterResource(id = characterImageRes),
                contentDescription = "캐릭터 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
        }

        // 말풍선
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = if (isUser) DiaViseoColors.Main1 else DiaViseoColors.Callout
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message,
                color = if (isUser) Color.White else DiaViseoColors.Basic,
                fontSize = 15.sp,
                textAlign = if (isUser) TextAlign.End else TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatMessageBubble_User() {
    ChatMessageBubble(
        message = "오늘 점심은 김치찌개였어요!",
        isUser = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewChatMessageBubble_Bot() {
    ChatMessageBubble(
        message = "안녕하세요! 식단을 평가해드릴게요 🍱",
        isUser = false,
        characterImageRes = R.drawable.charac_eat
    )
}
