package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** ChatMessage 데이터 클래스 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime,
    val characterImageRes: Int? = null
)

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (message.isUser) DiaViseoColors.Main1 else DiaViseoColors.Callout
    val textColor = if (message.isUser) Color.White else DiaViseoColors.Basic
    val timeFormatter = DateTimeFormatter.ofPattern("a h:mm")
    val formattedTime = message.timestamp.format(timeFormatter)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser && message.characterImageRes != null) {
            Image(
                painter = painterResource(id = message.characterImageRes),
                contentDescription = "캐릭터 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                BubbleTail(color = bubbleColor, isUser = false)
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = bubbleColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 15.sp,
                        textAlign = if (message.isUser) TextAlign.End else TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = DiaViseoColors.Placeholder
                )
            }

            if (message.isUser) {
                Spacer(modifier = Modifier.width(4.dp))
                BubbleTail(color = bubbleColor, isUser = true)
            }
        }
    }
}

@Composable
fun BubbleTail(color: Color, isUser: Boolean) {
    Canvas(
        modifier = Modifier
            .size(8.dp)
    ) {
        val path = Path().apply {
            if (isUser) {
                moveTo(0f, 0f)
                lineTo(size.width, size.height / 2)
                lineTo(0f, size.height)
            } else {
                moveTo(size.width, 0f)
                lineTo(0f, size.height / 2)
                lineTo(size.width, size.height)
            }
            close()
        }
        drawPath(path = path, color = color, style = Fill)
    }
}

@Composable
fun ScrollToBottomButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Text(
                text = "⬇ 아래로",
                fontSize = 13.sp,
                color = DiaViseoColors.Main1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatMessageBubble_User() {
    ChatMessageBubble(
        message = ChatMessage(
            text = "고마워!",
            isUser = true,
            timestamp = LocalDateTime.now()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewChatMessageBubble_Bot() {
    ChatMessageBubble(
        message = ChatMessage(
            text = "안녕하세요! 식단을 평가해드릴게요 🍱",
            isUser = false,
            timestamp = LocalDateTime.now(),
            characterImageRes = R.drawable.charac_eat
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewScrollToBottomButton() {
    ScrollToBottomButton(onClick = {})
}