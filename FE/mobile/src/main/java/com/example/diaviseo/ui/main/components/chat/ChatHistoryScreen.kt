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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.chat.ChatBotViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChatHistory(
    val id: String,
    val topic: ChatTopic,
    val lastMessage: String,
    val timestamp: LocalDateTime,
    val isEnded: Boolean = false
) : java.io.Serializable

@Composable
fun ChatHistoryScreen(navController: NavController) {
    val viewModel: ChatBotViewModel = viewModel()
    val histories by viewModel.histories.collectAsState()
    var selectedFilter by remember { mutableStateOf<ChatTopic?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchHistories()
    }

    val filteredHistories = selectedFilter?.let { topic ->
        histories.filter { it.topic == topic }
    } ?: histories

    ChatHistoryContent(
        histories = filteredHistories,
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it },
        onBackClick = { navController.popBackStack() },
        onChatClick = { selected ->
            navController.currentBackStackEntry?.savedStateHandle?.set("selectedHistory", selected)
            navController.navigate("chat")
        },
        onNewChatClick = {
            navController.currentBackStackEntry?.savedStateHandle?.set("selectedHistory", null)
            navController.navigate("chat")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryContent(
    histories: List<ChatHistory>,
    selectedFilter: ChatTopic?,
    onFilterSelected: (ChatTopic?) -> Unit,
    onChatClick: (ChatHistory) -> Unit,
    onBackClick: () -> Unit,
    onNewChatClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("과거 대화 기록", fontSize = 20.sp, color = DiaViseoColors.Basic, fontWeight = FontWeight.Bold)
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
            FilterTabRow(selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.height(8.dp))
            NewChatButton(onClick = onNewChatClick)
            Spacer(modifier = Modifier.height(12.dp))
            if (histories.isEmpty()) {
                EmptyHistoryMessage(selectedFilter)
            } else {
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
}

@Composable
private fun FilterTabRow(selected: ChatTopic?, onSelected: (ChatTopic?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val tabs = listOf<Pair<String, ChatTopic?>>("전체" to null, "식단이" to ChatTopic.DIET, "운동이" to ChatTopic.EXERCISE)
        tabs.forEach { (label, value) ->
            val isSelected = selected == value
            Button(
                onClick = { onSelected(value) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) DiaViseoColors.Main1 else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(label, color = Color.White)
            }
        }
    }
}

@Composable
private fun NewChatButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
    ) {
        Text("+ 새로운 대화 시작", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
private fun EmptyHistoryMessage(selectedFilter: ChatTopic?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (selectedFilter) {
                ChatTopic.DIET -> "식단이와의 대화 기록이 없어요"
                ChatTopic.EXERCISE -> "운동이와의 대화 기록이 없어요"
                null -> "아직 대화 기록이 없어요"
            },
            color = DiaViseoColors.Placeholder,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ChatHistoryCard(history: ChatHistory, onClick: () -> Unit) {
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
