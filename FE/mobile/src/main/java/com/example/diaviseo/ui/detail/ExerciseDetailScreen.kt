package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
//import com.example.diaviseo.viewmodel.ExerciseDetailViewModel
import com.example.diaviseo.viewmodel.GoalViewModel
import java.time.LocalDate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.google.android.gms.common.internal.service.Common

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ExerciseDetailScreen(
    navController: NavHostController,
//    viewModel: ExerciseDetailViewModel = viewModel(
//        factory = ExerciseDetailViewModel.provideFactory(selectedDate)
//    )
) {
    // GoalScreen이 속한 NavHost의 backStackEntry를 꺼내오기
    val goalBackStackEntry = remember {
        navController.getBackStackEntry("goal")
    }
    // 그 entry를 스코프로 해서 ViewModel을 가져오면
    val goalViewModel: GoalViewModel = viewModel(goalBackStackEntry)

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "$selectedDate 운동",
                onLeftActionClick = { navController.popBackStack() },
                onRightActionClick = { goalViewModel.setShowDatePicker() },
                calendar = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold 안쪽 padding 처리
                .navigationBarsPadding() // 하단 소프트바 대응
        ) {
            Text("여긴 운동 상세")
        }

        // 날짜 선택 모달
        DiaDatePickerDialog(
            isVisible = showDatePicker,
            initialDate = selectedDate,
            onDismiss = { goalViewModel.setShowDatePicker() },
            onConfirm = {
                goalViewModel.loadDataForDate(it)
                goalViewModel.setShowDatePicker()
            }
        )
    }
}
