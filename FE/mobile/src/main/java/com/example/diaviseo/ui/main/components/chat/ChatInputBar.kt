package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onFocusChanged: ((androidx.compose.ui.focus.FocusState) -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // 비활성화 시 흐려진 배경색 적용
    val backgroundColor = if (enabled) DiaViseoColors.Callout else DiaViseoColors.Callout.copy(alpha = 0.4f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .onFocusChanged { focusState -> onFocusChanged?.invoke(focusState) },
            placeholder = {
                Text(
                    text = "메시지를 입력하세요",
                    color = DiaViseoColors.Placeholder,
                    fontSize = 14.sp
                )
            },
            enabled = enabled,
            singleLine = false,
            maxLines = 3,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    keyboardController?.hide()
                    onSendClick()
                }
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color(0xFFF3F4F6).copy(alpha = 0.6f),
                disabledTextColor = Color.Gray.copy(alpha = 0.6f),
                disabledIndicatorColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                cursorColor = DiaViseoColors.Main1,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        IconButton(
            onClick = {
                keyboardController?.hide()
                onSendClick()
            },
            enabled = enabled && !isSending && inputText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = if (enabled && !isSending) DiaViseoColors.Main1 else DiaViseoColors.Placeholder
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatInputBarEnabled() {
    ChatInputBar(
        inputText = "헬로 챗봇",
        onInputChange = {},
        onSendClick = {},
        isSending = false,
        enabled = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewChatInputBarDisabled() {
    ChatInputBar(
        inputText = "비활성화 상태",
        onInputChange = {},
        onSendClick = {},
        isSending = false,
        enabled = false
    )
}
