package com.example.diaviseo.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.model.chat.ChatMessage
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.chatbot.dto.req.*
import com.example.diaviseo.network.chatbot.dto.res.*
import com.example.diaviseo.ui.main.components.chat.ChatHistory
import com.example.diaviseo.ui.main.components.chat.ChatTopic
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime

class ChatBotViewModel : ViewModel() {

    private val api = RetrofitInstance.chatBotApiService

    // 🔹 메시지 흐름
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    private val _sessionId = MutableStateFlow<String?>(null)
    val sessionId: StateFlow<String?> = _sessionId

    private val _isSessionEnded = MutableStateFlow(false)
    val isSessionEnded: StateFlow<Boolean> = _isSessionEnded

    var currentCharacterImageRes: Int? = null

    // 🔹 히스토리 흐름
    private val _histories = MutableStateFlow<List<ChatHistory>>(emptyList())
    val histories: StateFlow<List<ChatHistory>> = _histories

    fun fetchHistories() {
        viewModelScope.launch {
            try {
                val response = api.getChatSessions()
                _histories.value = response.map {
                    ChatHistory(
                        id = it.session_id,
                        topic = when (it.chatbot_type) {
                            "nutrition" -> ChatTopic.DIET
                            "workout" -> ChatTopic.EXERCISE
                            else -> error("Unknown chatbot_type: ${it.chatbot_type}")
                        },
                        lastMessage = "이전 대화 보기",
                        timestamp = LocalDateTime.parse(it.started_at),
                        isEnded = it.ended_at != null
                    )
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun startSession(type: String, characterImageRes: Int? = null) {
        viewModelScope.launch {
            try {
                val response = api.startSession(StartSessionRequest(type))
                _sessionId.value = response.session_id
                _messages.value = listOf(
                    ChatMessage(
                        text = response.welcome_message,
                        isUser = false,
                        timestamp = LocalDateTime.now(),
                        characterImageRes = characterImageRes
                    ),
                    ChatMessage(
                        text = "__SHOW_INITIAL_QUESTION_BUTTONS__",
                        isUser = false,
                        timestamp = LocalDateTime.now()
                    )
                )
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    fun sendMessage(text: String) {
        val sid = _sessionId.value ?: return
        _messages.value += ChatMessage(
            text = text,
            isUser = true,
            timestamp = LocalDateTime.now()
        )
        _isTyping.value = true

        viewModelScope.launch {
            try {
                val response = api.sendChatMessage(sid, ChatRequest(text))
                _messages.value += ChatMessage(
                    text = response.content,
                    isUser = false,
                    timestamp = LocalDateTime.parse(response.timestamp),
                    characterImageRes = currentCharacterImageRes
                )
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun endSession() {
        val sid = _sessionId.value ?: return
        viewModelScope.launch {
            try {
                api.endSession(EndSessionRequest(sid))
                _isSessionEnded.value = true
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun loadMessages(sessionId: String, characterImageRes: Int? = null, isEnded: Boolean = false) {
        viewModelScope.launch {
            try {
                val response = api.getChatMessages(sessionId)
                currentCharacterImageRes = characterImageRes
                _sessionId.value = sessionId
                _isSessionEnded.value = isEnded

                if (response.isEmpty()) {
                    if (!isEnded) {
                        _messages.value = listOf(
                            ChatMessage(
                                text = "안녕하세요! 어떤 내용이 궁금하신가요?",
                                isUser = false,
                                timestamp = LocalDateTime.now(),
                                characterImageRes = characterImageRes
                            ),
                            ChatMessage(
                                text = "__SHOW_INITIAL_QUESTION_BUTTONS__",
                                isUser = false,
                                timestamp = LocalDateTime.now()
                            )
                        )
                    } else {
                        _messages.value = listOf(
                            ChatMessage(
                                text = "\"안녕하세요! 어떤 내용이 궁금하신가요?",
                                isUser = false,
                                timestamp = LocalDateTime.now(),
                                characterImageRes = characterImageRes
                            )
                        )
                    }
                } else {
                    _messages.value = response.map {
                        ChatMessage(
                            text = it.content,
                            isUser = it.role == "user",
                            timestamp = LocalDateTime.parse(it.timestamp),
                            characterImageRes = if (it.role == "assistant") characterImageRes else null
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    private fun handleError(e: Exception) {
        val message = extractErrorMessage(e)
        _messages.value += ChatMessage(
            text = "❗ $message",
            isUser = false,
            timestamp = LocalDateTime.now()
        )
    }

    private fun extractErrorMessage(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).detail
                } catch (_: Exception) {
                    "오류 응답 파싱 실패"
                }
            }
            else -> e.message ?: "알 수 없는 오류 발생"
        }
    }

    fun removeInitialQuestionButtons() {
        _messages.value = _messages.value.filterNot { it.text == "__SHOW_INITIAL_QUESTION_BUTTONS__" }
    }

}
