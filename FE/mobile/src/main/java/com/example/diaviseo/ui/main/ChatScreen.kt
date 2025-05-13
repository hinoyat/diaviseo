package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.main.components.chat.*
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen() {
    var input by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    var selectedTopic by remember { mutableStateOf<ChatTopic?>(null) }
    var hasAskedFirstQuestion by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf<Pair<Boolean, String>>() // (isUser, message)
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (!showExitDialog) {
                ChatInputBar(
                    inputText = input,
                    onInputChange = { input = it },
                    onSendClick = {
                        if (input.isNotBlank()) {
                            messages.add(true to input)
                            input = ""
                            isTyping = true

                            coroutineScope.launch {
                                delay(1000)
                                messages.add(false to "이건 예시 챗봇 응답이에요 🍱")
                                isTyping = false
                            }
                        }
                    }

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
                // 고정 인트로
                if (selectedTopic == null) {
                    item {
                        FixedIntroScenario(
                            onSelectTopic = { topic ->
                                selectedTopic = topic
                            }
                        )
                    }
                }

                // 첫 질문 선택지
                if (selectedTopic != null && !hasAskedFirstQuestion) {
                    item {
                        InitialQuestionButtons(
                            onClick = { question ->
                                messages.add(true to question)
                                hasAskedFirstQuestion = true
                                isTyping = true

                                coroutineScope.launch {
                                    delay(800)
                                    messages.add(false to "이건 $question 에 대한 답변입니다! 😄")
                                    isTyping = false
                                }
                            }
                        )
                    }
                }

                // 대화 내역
                items(messages) { (isUser, msg) ->
                    ChatMessageBubble(
                        message = msg,
                        isUser = isUser,
                        characterImageRes = if (!isUser) {
                            when (selectedTopic) {
                                ChatTopic.DIET -> R.drawable.charac_eat
                                ChatTopic.EXERCISE -> R.drawable.charac_exercise
                                else -> null
                            }
                        } else null
                    )
                }

                // 입력 중 상태
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // 대화 종료 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { showExitDialog = true }) {
                    Text("대화 종료하기", color = DiaViseoColors.Red)
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
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}
