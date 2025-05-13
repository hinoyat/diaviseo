package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.home.BodyInfoCard
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


    // Dummy data - 추후 ViewModel 연동
    val skeletalMuscle: Double? = 32.2
    val userHeight = 165.9
    val userWeight = 57.9
    val bodyFat: Double? = 25.5 * 0.01 * userWeight
    val bmr = 1704.76500
    val goal = "WEIGHT_LOSS"
    val recommendedIntake = 1398
    val recommendedExercise = 418
    val totalCalorie = 1200
    val tdee = 1480
    val totalExerciseCalorie = 100
    val predictValue = totalCalorie - tdee - totalExerciseCalorie

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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BodyInfoCard(
                skeletalMuscle = skeletalMuscle,
                bodyFat = bodyFat,
                onSkeletalEdit = { /* TODO */ },
                onBodyFatEdit = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

//            DetailInfoCard(
//                nickname = nickname,
//                height = height,
//                weight = weight,
//                bmr = bmr,
//                onEditClick = { /* TODO */ }
//            )

            Spacer(modifier = Modifier.height(20.dp))
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(20.dp),
//                color = Color(0xFFEEEEEE)
//            )

            Spacer(modifier = Modifier.height(20.dp))

//            GoalSummaryCard(
//                nickname = nickname,
//                goal = goal,
//                recommendedIntake = recommendedIntake,
//                recommendedExercise = recommendedExercise,
//                totalCalorie = totalCalorie,
//                tdee = tdee,
//                totalExerciseCalorie = totalExerciseCalorie,
//                predictValue = predictValue
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // AiTipBox() 는 외부 컴포넌트로 처리
        }

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
