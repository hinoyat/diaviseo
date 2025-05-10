package com.example.diaviseo.ui.register.exercise.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.viewmodel.register.exercise.ExerciseRecordViewModel
import com.example.diaviseo.ui.theme.*
import androidx.compose.foundation.BorderStroke

@Composable
fun ExerciseRegisterBottomSheet(
    viewModel: ExerciseRecordViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val exercise = viewModel.selectedExercise.collectAsState().value ?: return
    val exerciseTime = viewModel.exerciseTime.collectAsState().value

    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("오전") }
    var periodMenuExpanded by remember { mutableStateOf(false) }

    val totalKcal = exercise.calorie * exerciseTime

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(text = exercise.name, style = bold20)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$totalKcal", style = bold20)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("kcal", style = medium14, modifier = Modifier.padding(start = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("소모", style = medium18)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("운동 시간", style = bold18)
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(48.dp)
                    .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("–", style = medium20, modifier = Modifier.clickable {
                        viewModel.setExerciseTime((exerciseTime - 5).coerceAtLeast(5))
                    })
                    Text("${exerciseTime} 분", style = bold16)
                    Text("+", style = medium20, modifier = Modifier.clickable {
                        viewModel.setExerciseTime(exerciseTime + 5)
                    })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("운동 시작 시간", style = bold18)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 오전/오후 선택
                Box {
                    OutlinedButton(
                        onClick = { periodMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(period, style = medium14)
                    }

                    DropdownMenu(
                        expanded = periodMenuExpanded,
                        onDismissRequest = { periodMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("오전", "오후").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = medium14) },
                                onClick = {
                                    period = option
                                    periodMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = hour,
                    onValueChange = {
                        if (it.length <= 2 && it.all(Char::isDigit)) hour = it
                    },
                    modifier = Modifier.width(70.dp).height(56.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = DiaViseoColors.Main1,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Text("시", style = regular14)

                OutlinedTextField(
                    value = minute,
                    onValueChange = {
                        if (it.length <= 2 && it.all(Char::isDigit)) minute = it
                    },
                    modifier = Modifier.width(70.dp).height(56.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = DiaViseoColors.Main1,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Text("분", style = regular14)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val rawHour = hour.toIntOrNull() ?: 0
                    val correctedHour = if (period == "오후" && rawHour in 1..11) rawHour + 12 else rawHour
                    val formattedTime = String.format("%02d:%02d", correctedHour, minute.toIntOrNull() ?: 0)

                    viewModel.setStartTime(correctedHour.toString(), minute)
                    viewModel.submitExercise(
                        onSuccess = {
                            onDismiss()
                            onSuccess()
                        },
                        onError = {
                            Log.e("ExerciseSubmit", "에러 발생: $it")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiaViseoColors.Main1,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("기록하기", style = regular16)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
