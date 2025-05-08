package com.example.diaviseo.ui.mypageedit.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditNicknameBottomSheet(
    initialNickname: String = "김디아",
    onSave: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var nickname by remember { mutableStateOf(TextFieldValue(initialNickname)) }

    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "닉네임 변경",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(text = "닉네임", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { if (it.text.length <= 8) nickname = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${nickname.text.length}/8",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(nickname.text) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("확인")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditNicknameBottomSheetPreview() {
    EditNicknameBottomSheet()
}
