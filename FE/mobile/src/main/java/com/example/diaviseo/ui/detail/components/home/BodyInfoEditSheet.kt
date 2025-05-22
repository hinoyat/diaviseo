package com.example.diaviseo.ui.detail.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.*

@Composable
fun BodyInfoEditSheet(
    title: String,
    initialValue: String = "",
    unit: String,
    onConfirm: (Double?) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf(initialValue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = bold18,
            color = DiaViseoColors.Basic
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            textStyle = medium20,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(unit, style = medium16) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        MainButton(
            text = "확인",
            onClick = {
                val parsed = input.toDoubleOrNull()
                onConfirm(parsed)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onDismiss) {
            Text("취소", style = medium14, color = DiaViseoColors.Unimportant)
        }
    }
}
