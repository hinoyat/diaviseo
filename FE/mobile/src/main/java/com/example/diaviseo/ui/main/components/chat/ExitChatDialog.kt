package com.example.diaviseo.ui.main.components.chat

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun ExitChatDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "대화종료 확인",
                color = DiaViseoColors.Basic
            )
        },
        text = {
            Text(
                text = "정말 대화를 종료하시겠습니까?",
                color = DiaViseoColors.Basic
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("예", color = DiaViseoColors.Main1)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("아니요", color = DiaViseoColors.Main1)
            }
        },
        containerColor = Color.White
    )
}
