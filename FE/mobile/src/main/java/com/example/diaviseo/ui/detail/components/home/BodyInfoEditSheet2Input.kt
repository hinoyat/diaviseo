package com.example.diaviseo.ui.detail.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold18
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.medium20
import com.example.diaviseo.ui.theme.semibold20


@Composable
fun BodyInfoEditSheet2Input(
    heightInitValue: String,
    weightInitValue: String,
    onConfirm: (height: String, weight: String) -> Unit,
    onDismiss: () -> Unit
) {
    var height by remember { mutableStateOf(heightInitValue) }
    var weight by remember { mutableStateOf(weightInitValue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "신체 정보 수정",
            style = bold18,
            color = DiaViseoColors.Basic
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("키(cm)") },
            textStyle = medium20,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("몸무게(kg)") },
            textStyle = medium20,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))


        MainButton(
            text = "확인",
            onClick = {
                onConfirm(height, weight)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onDismiss) {
            Text("취소", style = medium14, color = DiaViseoColors.Unimportant)
        }
    }
}
