package com.example.diaviseo.ui.mypageedit.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun EditNicknameBottomSheet(
    initialNickname: String = "",
    onSave: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var nickname by remember { mutableStateOf(TextFieldValue(initialNickname)) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Text(
                text = "닉네임 변경",
                fontSize = 18.sp,
                color = DiaViseoColors.Basic,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "닉네임",
                fontSize = 14.sp,
                color = DiaViseoColors.Unimportant
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { if (it.text.length <= 8) nickname = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("최대 8자까지 입력") }
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${nickname.text.length}/8",
                fontSize = 12.sp,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(nickname.text) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
            ) {
                Text("확인", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditNicknameBottomSheetPreview() {
    EditNicknameBottomSheet(initialNickname = "김디아")
}
