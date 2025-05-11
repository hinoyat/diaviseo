package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.main.components.goal.DonutChartWithLegend
import com.example.diaviseo.viewmodel.GoalViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MealDetailScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
//    viewModel: ExerciseDetailViewModel = viewModel(
//        factory = ExerciseDetailViewModel.provideFactory(selectedDate)
//    )
) {
    // 1) 먼저 이전 엔트리를 확인해본다
    val previousEntry = navController.previousBackStackEntry

    // 2) 이전 엔트리가 "goal" 또는 "home"이면 그걸 사용, 아니면 getBackStackEntry()로 대체
    val parentEntry = remember(previousEntry, navController) {
        when (previousEntry?.destination?.route) {
            "goal", "home" -> previousEntry
            else -> runCatching { navController.getBackStackEntry("goal") }
                .getOrElse {
                    runCatching { navController.getBackStackEntry("home") }
                        .getOrNull()
                }
        } ?: error("No ‘goal’ or ‘home’ in back stack")
    }

    // 3) 찾은 entry 로부터 ViewModel 을 불러온다
    val goalViewModel: GoalViewModel = viewModel(parentEntry)

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    // 닉네임 가져오게 viewModel : profileviewmodel
    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

    // 몇월 며칠 날짜 파싱
    val day = selectedDate.dayOfMonth.toString()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "${selectedDate.monthValue}월 ${day}일 식단",
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DonutChartWithLegend(
                calories = 689,
                calorieGoal = 1285,
                carbRatio = 0.3f,
                sugarRatio = 0.1f,
                proteinRatio = 0.1f,
                fatRatio = 0.2f
            )
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