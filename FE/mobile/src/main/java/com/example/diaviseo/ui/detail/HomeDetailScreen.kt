package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.viewmodel.ProfileViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun HomeDetailScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val goalViewModel: GoalViewModel = viewModel()

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    // 닉네임 가져오게 viewModel : profileviewmodel
    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }
    // 몇월 며칠 날짜 파싱
    val formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN)
    val formatted = selectedDate.format(formatter)


    Scaffold(
        topBar = {
            CommonTopBar(
                title = formatted,
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
                .verticalScroll(rememberScrollState())
        ) {
            Text("여긴 홈 디뗑일")
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

