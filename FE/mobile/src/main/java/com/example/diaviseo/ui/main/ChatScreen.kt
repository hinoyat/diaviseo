package com.example.diaviseo.ui.main

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var isDetailMode by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val isSessionEnded by viewModel.isSessionEnded.collectAsState()
    val sessionId by viewModel.sessionId.collectAsState()

    var selectedTopic by remember { mutableStateOf<ChatTopic?>(history?.topic) }
    var showExitDialog by remember { mutableStateOf(false) }
    val isInputFocused = remember { mutableStateOf(false) }
    var showDetailTooltip by remember { mutableStateOf(false) }
    var shouldHideTooltip by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (history != null) {
            viewModel.loadMessages(
                sessionId = history.id,
                characterImageRes = when (history.topic) {
                    ChatTopic.DIET -> R.drawable.chat_char_diet
                    ChatTopic.EXERCISE -> R.drawable.chat_char_exercise
                },
                isEnded = history.isEnded
            )
        }
    }
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp),
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
                                topic = selectedTopic,
                                onClick = { question ->
                                    viewModel.removeInitialQuestionButtons()
                                    if (question != "직접 입력할게요") {
                                        viewModel.sendMessage(question)
                                    }
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
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()

                ) {
                    if (isInputFocused.value) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                AnimatedVisibility(
                                    visible = showDetailTooltip,
                                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                                ) {
                                    LaunchedEffect(shouldHideTooltip) {
                                        if (shouldHideTooltip) {
                                            kotlinx.coroutines.delay(300) // exit 애니메이션 시간
                                            showDetailTooltip = false
                                            shouldHideTooltip = false
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "자세히 모드는 더 깊이 있는 답변을 받을 수 있도록 도와줘요.",
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }


                                // ✅ 자세히 모드 버튼
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            isDetailMode = !isDetailMode
                                            if (isDetailMode) {
                                                showDetailTooltip = true
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.delay(3000)
                                                    shouldHideTooltip = true
                                                }
                                            } else {
                                                shouldHideTooltip = true
                                            }
                                        }
                                        .border(
                                            width = 1.dp,
                                            color = if (isDetailMode) DiaViseoColors.Main1 else DiaViseoColors.Placeholder,
                                            shape = RoundedCornerShape(20.dp)
                                        ),
                                    color = Color.White,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "자세히 모드",
                                        color = if (isDetailMode) DiaViseoColors.Main1 else DiaViseoColors.Placeholder,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                    }
                    ChatInputBar(
                        inputText = inputState.value,
                        onInputChange = { inputState.value = it },
                        onSendClick = {
                            val userMessage = inputState.value
                            val finalMessage = if (isDetailMode) "자세히 $userMessage" else userMessage

                            viewModel.sendMessage(finalMessage)
                            viewModel.removeInitialQuestionButtons()

                            inputState.value = ""
                            isDetailMode = false
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        isSending = isTyping,
                        enabled = !isSessionEnded,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onFocusChanged = { focusState -> isInputFocused.value = focusState.isFocused }
                    )
                }
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
