package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.model.chat.ChatMessage
import com.example.diaviseo.ui.main.components.chat.*
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.chat.ChatBotViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(navController: NavController) {
    val viewModel: ChatBotViewModel = viewModel()
    val history = navController.previousBackStackEntry?.savedStateHandle?.get<ChatHistory>("selectedHistory")

    ChatContent(
        history = history,
        viewModel = viewModel,
        onBackClick = { navController.popBackStack() },
        onExitClick = { navController.navigate("chat_history") }
    )
}

@Composable
fun ChatContent(
    history: ChatHistory?,
    viewModel: ChatBotViewModel,
    onBackClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val inputState = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val isSessionEnded by viewModel.isSessionEnded.collectAsState()
    val sessionId by viewModel.sessionId.collectAsState()

    var selectedTopic by remember { mutableStateOf<ChatTopic?>(history?.topic) }
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (history != null) {
            viewModel.loadMessages(
                sessionId = history.id,
                characterImageRes = when (history.topic) {
                    ChatTopic.DIET -> R.drawable.chat_char_diet
                    ChatTopic.EXERCISE -> R.drawable.chat_char_exercise
                },
                isEnded = history.isEnded // 🔥 종료 여부 반영
            )
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackClick = onBackClick,
                onExitClick = { showExitDialog = true }
            )
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
                if (sessionId == null && selectedTopic == null) {
                    item {
                        FixedIntroScenario(onSelectTopic = { topic ->
                            selectedTopic = topic
                            viewModel.startSession(
                                type = when (topic) {
                                    ChatTopic.DIET -> "nutrition"
                                    ChatTopic.EXERCISE -> "workout"
                                },
                                characterImageRes = when (topic) {
                                    ChatTopic.DIET -> R.drawable.chat_char_diet
                                    ChatTopic.EXERCISE -> R.drawable.chat_char_exercise
                                }
                            )
                        })
                    }
                }

                itemsIndexed(messages) { index, msg ->
                    Column {
                        if (index == 0 || isNewDay(messages[index - 1], msg)) {
                            ChatDateDivider(date = msg.timestamp.toLocalDate())
                        }
                        if (msg.text == "__SHOW_INITIAL_QUESTION_BUTTONS__") {
                            InitialQuestionButtons(
                                topic = selectedTopic,
                                onClick = { question ->
                                    viewModel.sendMessage(question)
                                }
                            )
                        } else {
                            ChatMessageBubble(message = msg)
                        }
                    }
                }

                if (isTyping) {
                    item { TypingIndicator() }
                }
            }

            if (!showExitDialog && sessionId != null) {
                key(isSessionEnded) {
                    ChatInputBar(
                        inputText = inputState.value,
                        onInputChange = { inputState.value = it },
                        onSendClick = {
                            viewModel.sendMessage(inputState.value)
                            inputState.value = ""
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        isSending = isTyping,
                        enabled = !isSessionEnded,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }


            if (showExitDialog) {
                ExitChatDialog(
                    onConfirm = {
                        viewModel.endSession()
                        showExitDialog = false
                        inputState.value = ""
                        onExitClick()
                    },
                    onDismiss = { showExitDialog = false }
                )
            }
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