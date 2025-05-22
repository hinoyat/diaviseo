package com.example.diaviseo.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaDatePickerDialog(
    isVisible: Boolean,
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    if (!isVisible) return

    // DatePicker 상태 기억
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000,
        yearRange = 1900..LocalDate.now().year
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
    }

    val bluePoint = Color(0xFF1673FF)
    val darkText = Color(0xFF222222)
    val grayText = Color(0xFFAFAFAF)

    val customColors = DatePickerDefaults.colors(
        containerColor = Color.White,
        titleContentColor = darkText,
        headlineContentColor = darkText,
        weekdayContentColor = darkText,
        subheadContentColor = darkText,
        yearContentColor = darkText,
        currentYearContentColor = bluePoint,
        selectedYearContentColor = Color.White,
        selectedYearContainerColor = bluePoint,
        dayContentColor = darkText,
        disabledDayContentColor = Color.LightGray,
        selectedDayContentColor = Color.White,
        selectedDayContainerColor = bluePoint,
        todayContentColor = darkText,
        todayDateBorderColor = bluePoint
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = customColors,
        confirmButton = {
            TextButton(
                onClick = { selectedDate?.let(onConfirm) },
                enabled = selectedDate != null && !selectedDate.isAfter(LocalDate.now())
            ) {
                Text("이동하기", color = if (selectedDate != null && !selectedDate.isAfter(LocalDate.now())) bluePoint else grayText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = darkText)
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            colors = customColors
        )
    }
}
