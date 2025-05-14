package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTimePickerBottomSheet(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val focusManager = LocalFocusManager.current

    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("오전") }

    ModalBottomSheet(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .padding(vertical = 20.dp, horizontal = 22.dp)
        ) {
            Text(
                text = "식사시간 기록하기",
                style = bold20,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 오전 / 오후 선택 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("오전", "오후").forEach { label ->
                    Text(
                        text = label,
                        style = if (period == label) bold16 else regular16,
                        color = if (period == label) Color.Black else Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { period = label }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 시 / 분 입력 필드
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hour,
                    onValueChange = {
                        if (it.length <= 2 && it.all(Char::isDigit)) {
                            val number = it.toIntOrNull()
                            if (number == null || number in 1..12) hour = it
                        }
                    },
                    label = {
                        Text(
                            "시",
                            color = DiaViseoColors.Main1
                        )
                            },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DiaViseoColors.Main1,
                        unfocusedBorderColor = DiaViseoColors.Main1
                    )
                )

                OutlinedTextField(
                    value = minute,
                    onValueChange = {
                        if (it.length <= 2 && it.all(Char::isDigit)) {
                            val number = it.toIntOrNull()
                            if (number == null || number in 0..59) minute = it
                        }
                    },
                    label = {
                        Text(
                            "분",
                            color = DiaViseoColors.Main1
                        ) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DiaViseoColors.Main1,
                        unfocusedBorderColor = DiaViseoColors.Main1
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 확인 버튼
            MainButton(
                text = "확인",
                onClick = {
                    val formattedTime =
                        "${hour.padStart(2, '0')}:${minute.padStart(2, '0')} $period"
                    focusManager.clearFocus()
                    onTimeSelected(formattedTime)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hour.isNotBlank() && minute.isNotBlank()
            )
        }
    }
}
