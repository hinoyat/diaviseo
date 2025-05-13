package com.example.diaviseo.ui.main.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = DiaViseoColors.Callout,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            placeholder = {
                Text(
                    text = "메시지를 입력하세요",
                    color = DiaViseoColors.Placeholder,
                    fontSize = 14.sp
                )
            },
            enabled = !isSending,
            singleLine = false,
            maxLines = 3,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                cursorColor = DiaViseoColors.Main1,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Text(
            text = "전송",
            fontSize = 14.sp,
            color = if (isSending) DiaViseoColors.Placeholder else DiaViseoColors.Main1,
            modifier = Modifier
                .clickable(enabled = !isSending && inputText.isNotBlank()) {
                    onSendClick()
                }
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatInputBar() {
    ChatInputBar(
        inputText = "",
        onInputChange = {},
        onSendClick = {},
        isSending = false
    )
}
