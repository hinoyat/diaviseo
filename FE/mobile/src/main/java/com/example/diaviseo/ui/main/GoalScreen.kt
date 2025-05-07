package com.example.diaviseo.ui.main

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import java.time.LocalDate
import com.example.diaviseo.ui.main.components.goal.*
import com.example.diaviseo.viewmodel.GoalViewModel

@Composable
fun GoalScreen(navController: NavHostController) {
    val selectedTab = remember { mutableStateOf("식단") }
    val today = remember { LocalDate.now() }
    val goalViewModel: GoalViewModel = viewModel()

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            GoalTopHeader(
                currentDate = today,
                selectedTab = selectedTab.value,
                onTabChange = { selectedTab.value = it },
                viewModel = goalViewModel
            )
            GoalContent(
                selectedTab = selectedTab.value,
                today = today
            )
        }
        // 날짜 선택 모달
        DiaDatePickerDialog(
            isVisible = showDatePicker,
            initialDate = today,
            onDismiss = { goalViewModel.setShowDatePicker() },
            onConfirm = {
                goalViewModel.loadDataForDate(it)
                goalViewModel.setShowDatePicker()
            }
        )
    }
}
