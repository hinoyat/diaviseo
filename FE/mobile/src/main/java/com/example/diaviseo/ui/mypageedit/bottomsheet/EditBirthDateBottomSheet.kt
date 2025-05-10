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
fun EditBirthDateBottomSheet(
    initialBirthDate: String = "2000.02.14",
    onSave: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var birthDate by remember { mutableStateOf(TextFieldValue(initialBirthDate)) }

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
                text = "생년월일 변경",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(text = "생년월일(8자리)", fontSize = 14.sp)
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("확인")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditBirthDateBottomSheetPreview() {
    EditBirthDateBottomSheet()
}
