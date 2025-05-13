package com.example.diaviseo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.main.components.chat.*
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ✅ 외부에서 navController만 받는 wrapper
@Composable
fun ChatScreen(navController: NavController) {
    val history = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<ChatHistory>("selectedHistory")

    ChatContent(
        history = history,
        onBackClick = { navController.popBackStack() },
        onExitClick = { navController.navigate("chatHistory") }
    )
}

// ✅ UI/상태 포함한 본문
@Composable
fun ChatContent(
    history: ChatHistory?,
    onBackClick: () -> Unit,
    onExitClick: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var selectedTopic by remember { mutableStateOf<ChatTopic?>(history?.topic) }
    var hasAskedFirstQuestion by remember { mutableStateOf(history != null) }

    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            history?.let {
                add(
                    ChatMessage(
                        text = it.lastMessage,
                        isUser = true,
                        timestamp = it.timestamp
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackClick = onBackClick,
                onExitClick = { showExitDialog = true }
            )
        },
        bottomBar = {
            if (!showExitDialog) {
                ChatInputBar(
                    inputText = input,
                    onInputChange = { input = it },
                    onSendClick = {
                        if (input.isNotBlank()) {
                            messages.add(
                                ChatMessage(
                                    text = input,
                                    isUser = true,
                                    timestamp = LocalDateTime.now()
                                )
                            )
                            input = ""
                            isTyping = true

                            coroutineScope.launch {
                                delay(1000)
                                messages.add(
                                    ChatMessage(
                                        text = "이건 예시 챗봇 응답이에요 🍱",
                                        isUser = false,
                                        timestamp = LocalDateTime.now(),
                                        characterImageRes = when (selectedTopic) {
                                            ChatTopic.DIET -> R.drawable.charac_eat
                                            ChatTopic.EXERCISE -> R.drawable.charac_exercise
                                            else -> null
                                        }
                                    )
                                )
                                isTyping = false
                            }
                        }
                    },
                    isSending = isTyping
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                reverseLayout = false
            ) {
                if (selectedTopic == null) {
                    item {
                        FixedIntroScenario(
                            onSelectTopic = { topic ->
                                selectedTopic = topic
                            }
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = selectedTopic != null && !hasAskedFirstQuestion,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        InitialQuestionButtons(
                            onClick = { question ->
                                messages.add(
                                    ChatMessage(
                                        text = question,
                                        isUser = true,
                                        timestamp = LocalDateTime.now()
                                    )
                                )
                                hasAskedFirstQuestion = true
                                isTyping = true

                                coroutineScope.launch {
                                    delay(800)
                                    messages.add(
                                        ChatMessage(
                                            text = "이건 $question 에 대한 답변입니다! 😄",
                                            isUser = false,
                                            timestamp = LocalDateTime.now(),
                                            characterImageRes = when (selectedTopic) {
                                                ChatTopic.DIET -> R.drawable.charac_eat
                                                ChatTopic.EXERCISE -> R.drawable.charac_exercise
                                                else -> null
                                            }
                                        )
                                    )
                                    isTyping = false
                                }
                            }
                        )
                    }
                }

                itemsIndexed(messages) { index, msg ->
                    Column {
                        if (index == 0 || isNewDay(messages[index - 1], msg)) {
                            ChatDateDivider(date = msg.timestamp.toLocalDate())
                        }
                        ChatMessageBubble(message = msg)
                    }
                }

                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }

        if (showExitDialog) {
            ExitChatDialog(
                onConfirm = {
                    showExitDialog = false
                    input = ""
                    messages.clear()
                    selectedTopic = null
                    hasAskedFirstQuestion = false
                    onExitClick()
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}

@Composable
fun ChatDateDivider(date: LocalDate) {
    val label = when (date) {
        LocalDate.now() -> "오늘"
        LocalDate.now().minusDays(1) -> "어제"
        else -> date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    Text(
        text = label,
        fontSize = 13.sp,
        color = DiaViseoColors.Placeholder,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        textAlign = TextAlign.Center
    )
}

fun isNewDay(prev: ChatMessage, current: ChatMessage): Boolean {
    return prev.timestamp.toLocalDate() != current.timestamp.toLocalDate()
}

@Preview(showBackground = true)
@Composable
fun PreviewChatDateDivider() {
    ChatDateDivider(date = LocalDate.now())
}
