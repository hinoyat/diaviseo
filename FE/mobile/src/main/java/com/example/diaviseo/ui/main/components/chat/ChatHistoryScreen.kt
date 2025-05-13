package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaviseo.ui.theme.DiaViseoColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ChatHistory data class remains unchanged
data class ChatHistory(
    val id: String,
    val topic: ChatTopic,
    val lastMessage: String,
    val timestamp: LocalDateTime
) : java.io.Serializable

// ✅ Wrapping composable that handles NavController logic
@Composable
fun ChatHistoryScreen(navController: NavController) {
    val mockHistories = remember {
        listOf(
            ChatHistory("1", ChatTopic.DIET, "오늘 뭐 먹지?", LocalDateTime.now().minusHours(1)),
            ChatHistory("2", ChatTopic.EXERCISE, "하체 루틴 뭐할까", LocalDateTime.now().minusDays(1))
        )
    }

    ChatHistoryContent(
        histories = mockHistories,
        onBackClick = { navController.popBackStack() },
        onChatClick = { selected ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("selectedHistory", selected)
            navController.navigate("chat")
        },
        onNewChatClick = {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("selectedHistory", null)
            navController.navigate("chat")
        }
    )
}

// ✅ UI-only composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryContent(
    histories: List<ChatHistory>,
    onChatClick: (ChatHistory) -> Unit,
    onBackClick: () -> Unit,
    onNewChatClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("AI 디아비서", fontSize = 20.sp, color = DiaViseoColors.Basic, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기", tint = DiaViseoColors.Basic)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNewChatClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
            ) {
                Text("+ 새로운 대화 시작", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(histories) { history ->
                    ChatHistoryCard(history = history, onClick = { onChatClick(history) })
                }
            }
        }
    }
}

@Composable
fun ChatHistoryCard(history: ChatHistory, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = when (history.topic) {
                ChatTopic.DIET -> "🥗 식단이와의 대화"
                ChatTopic.EXERCISE -> "🏋 운동이와의 대화"
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = DiaViseoColors.Basic
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = history.lastMessage,
            fontSize = 14.sp,
            color = DiaViseoColors.Placeholder,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = history.timestamp.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")),
            fontSize = 12.sp,
            color = DiaViseoColors.Placeholder
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatHistoryScreen() {
    val mockData = listOf(
        ChatHistory("1", ChatTopic.DIET, "오늘 점심은 뭘 먹을까?", LocalDateTime.now().minusHours(1)),
        ChatHistory("2", ChatTopic.EXERCISE, "하체 운동 추천해줘", LocalDateTime.now().minusDays(1))
    )
    ChatHistoryContent(
        histories = mockData,
        onChatClick = {},
        onBackClick = {},
        onNewChatClick = {}
    )
}
