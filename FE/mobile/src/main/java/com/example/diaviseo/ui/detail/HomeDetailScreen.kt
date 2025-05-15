package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.home.BodyInfoCard
import com.example.diaviseo.ui.detail.components.home.BodyInfoEditSheet
import com.example.diaviseo.ui.detail.components.home.DetailInfoCard
import com.example.diaviseo.ui.detail.components.home.GoalSummaryCard
import com.example.diaviseo.ui.main.components.goal.AiTipBox
import com.example.diaviseo.viewmodel.ProfileViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    var skeletalMuscle by remember { mutableStateOf<Double?>(32.2) }
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

    var showMuscleSheet by remember { mutableStateOf(false) }
    var showFatSheet by remember { mutableStateOf(false) }

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
                onSkeletalEdit = { showMuscleSheet = true },
                onBodyFatEdit = { showFatSheet = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailInfoCard(
                nickname = nickname.toString(),
                height = userHeight,
                weight = userWeight,
                bmr = bmr,
                onEditClick = {
                    navController.navigate("edit_physical_info") {
                        launchSingleTop = true
                        restoreState = true
//                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
//                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(color = Color(0xFFEEEEEE))
            ) {}

            Spacer(modifier = Modifier.height(20.dp))

            GoalSummaryCard(
                nickname = nickname.toString(),
                goal = goal,
                recommendedIntake = recommendedIntake,
                recommendedExercise = recommendedExercise,
                totalCalorie = totalCalorie,
                tdee = tdee,
                totalExerciseCalorie = totalExerciseCalorie,
                predictValue = predictValue
            )

            Spacer(modifier = Modifier.height(72.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                AiTipBox(
                    message = "안녕", // 또는 null로 빈 상태 테스트
                    onRequestFeedback = {
                        // 피드백 요청 처리
                        // 예: goalViewModel.requestAiFeedback()
                    }
                )
            }
        }


        if (showFatSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFatSheet = false },
                containerColor = Color.Transparent
            ) {
                BodyInfoEditSheet(
                    title = "체지방량 수정",
                    unit = "kg",
                    initialValue = bodyFat?.toString() ?: "",
                    onConfirm = {
                        // TODO: 체지방량은 계산 기반 값이라 바로 갱신 불가
                        showFatSheet = false
                    },
                    onDismiss = { showFatSheet = false }
                )
            }
        }

        if (showMuscleSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMuscleSheet = false },
                containerColor = Color.Transparent
            ) {
                BodyInfoEditSheet(
                    title = "골격근량 수정",
                    unit = "kg",
                    initialValue = skeletalMuscle?.toString() ?: "",
                    onConfirm = { value ->
                        skeletalMuscle = value // 업데이트 처리
                        showMuscleSheet = false
                    },
                    onDismiss = { showMuscleSheet = false }
                )
            }
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
