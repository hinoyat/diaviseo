package com.example.diaviseo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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

@Composable
fun ChatScreen(navController: NavController) {
    val history = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<ChatHistory>("selectedHistory")

    ChatContent(
        history = history,
        onBackClick = { navController.popBackStack() },
        onExitClick = { navController.navigate("chat_history") }
    )
}

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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
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
                            keyboardController?.hide()
                            focusManager.clearFocus()

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
                state = listState,
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
                                messages.add(
                                    ChatMessage(
                                        text = when (topic) {
                                            ChatTopic.DIET -> "식단이🥗를 골라주셨어요! 어떤 질문으로 시작해볼까요?"
                                            ChatTopic.EXERCISE -> "운동이💪를 골라주셨어요! 어떤 질문으로 시작해볼까요?"
                                        },
                                        isUser = false,
                                        timestamp = LocalDateTime.now(),
                                        characterImageRes = when (topic) {
                                            ChatTopic.DIET -> R.drawable.charac_eat
                                            ChatTopic.EXERCISE -> R.drawable.charac_exercise
                                        }
                                    )
                                )
                                messages.add(
                                    ChatMessage(
                                        text = "__SHOW_INITIAL_QUESTION_BUTTONS__",
                                        isUser = false,
                                        timestamp = LocalDateTime.now()
                                    )
                                )
                            }
                        )
                    }
                }

                itemsIndexed(messages) { index, msg ->
                    Column {
                        if (index == 0 || isNewDay(messages[index - 1], msg)) {
                            ChatDateDivider(date = msg.timestamp.toLocalDate())
                        }
                        if (msg.text == "__SHOW_INITIAL_QUESTION_BUTTONS__") {
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
                        } else {
                            ChatMessageBubble(message = msg)
                        }
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