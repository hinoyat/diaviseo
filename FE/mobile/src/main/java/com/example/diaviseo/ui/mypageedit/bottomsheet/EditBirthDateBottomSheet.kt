package com.example.diaviseo.ui.mypageedit.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun EditBirthDateBottomSheet(
    initialBirthDate: String = "",
    onSave: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var birthDate by remember { mutableStateOf(TextFieldValue(initialBirthDate)) }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Text(
                text = "생년월일 변경",
                fontSize = 18.sp,
                color = DiaViseoColors.Basic,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "생년월일 (8~10자리)",
                fontSize = 14.sp,
                color = DiaViseoColors.Unimportant
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { if (it.text.length <= 10) birthDate = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("예: 2000.01.01") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(birthDate.text) },
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
fun EditBirthDateBottomSheetPreview() {
    EditBirthDateBottomSheet(initialBirthDate = "2000.01.01")
}
