package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import com.example.diaviseo.network.body.dto.req.BodyUpdateRequest
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.home.BodyInfoCard
import com.example.diaviseo.ui.detail.components.home.BodyInfoEditSheet
import com.example.diaviseo.ui.detail.components.home.BodyInfoEditSheet2Input
import com.example.diaviseo.ui.detail.components.home.DetailInfoCard
import com.example.diaviseo.ui.detail.components.home.GoalSummaryCard
import com.example.diaviseo.ui.main.components.goal.AiTipBox
import com.example.diaviseo.viewmodel.HomeViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.WeightViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry", "DefaultLocale")
@Composable
fun HomeDetailScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val previousEntry = navController.previousBackStackEntry
        ?: error("이전 스크린이 없습니다")

    val goalViewModel: GoalViewModel = viewModel()
    val weightViewModel: WeightViewModel = viewModel()
    val homeViewModel : HomeViewModel = viewModel()

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    val workoutFeedback by goalViewModel.workoutFeedback.collectAsState()
    val isWorkLoading by goalViewModel.isWorkLoading.collectAsState()

    val physicalInfo by weightViewModel.physicalInfo.collectAsState()
    val bodyLatestInfo by weightViewModel.bodyLatestInfo.collectAsState()

    val isToday = remember(selectedDate) {
        selectedDate == LocalDate.now()
    }

    LaunchedEffect(selectedDate, bodyLatestInfo) {
        coroutineScope {
            async { homeViewModel.fetchDailyNutrition(selectedDate.toString()) }
            async { homeViewModel.fetchDailyExercise(selectedDate.toString()) }
            async { weightViewModel.fetchPhysicalInfo(selectedDate.toString()) }
            async { weightViewModel.loadLatestBodyData(selectedDate.toString()) }
            // 오늘 조회일 때만 체중 예측 코멘트가 보여요
            if (isToday) {
                async { goalViewModel.isThereFeedback("workout", selectedDate.toString()) }
            } else {
                // 오늘이 아니면 체중 피드백 "" 로 바꾸기 == 팁 박스 안 뜸
                async { goalViewModel.setWorkoutFeedback() }
            }
        }
    }

    val totalHomeCalorie by homeViewModel.totalCalorie.collectAsState()
    val totalHomeExerciseCalorie by homeViewModel.totalExerciseCalorie.collectAsState()

    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    // 선택날 체정분 데이터, bodyLatestInfo.measurementDate 가 오늘이 아니라면
    // 화면에 보이는 체지방, 골격근 0
    // 수정하면 하나만 수정되긴 할텐데, weight는 bodyLatestInfo.weight, measurementDate는 selectedDate patch
    // 근데 bodyLatestInfo.bodyId 가 null이면 post로 보내야해
    // 그리고 수정하고 등록하면 다시 위에 lauch 2개 동일하게 해야 해

    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

    // 몇월 며칠 날짜 파싱
    val formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN)
    val formatted = selectedDate.format(formatter)

    var muscleMass = bodyLatestInfo?.muscleMass
    val userHeight = bodyLatestInfo?.height
    val userWeight = bodyLatestInfo?.weight
    val bodyFat: Double? = bodyLatestInfo?.bodyFat
    val bmr = bodyLatestInfo?.bmr
    val goal = physicalInfo?.goal
    val recommendedIntake = physicalInfo?.recommendedIntake
    val recommendedExercise = physicalInfo?.recommendedExercise
    val totalCalorie = totalHomeCalorie
    val tdee = if (physicalInfo != null) physicalInfo?.tdee else 0
    val totalExerciseCalorie = totalHomeExerciseCalorie
    val predictValue = totalCalorie - tdee!!.toInt() - totalExerciseCalorie

    var showMuscleSheet by remember { mutableStateOf(false) }
    var showFatSheet by remember { mutableStateOf(false) }
    var showHWSheet by remember { mutableStateOf(false) }

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
                skeletalMuscle = muscleMass,
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
                onEditClick = { showHWSheet = true }
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
                goal = goal.toString(),
                recommendedIntake = recommendedIntake,
                recommendedExercise = recommendedExercise,
                totalCalorie = totalCalorie,
                tdee = tdee,
                totalExerciseCalorie = totalExerciseCalorie,
                predictValue = predictValue
            )

            Spacer(
                modifier = Modifier.height(
                    if (workoutFeedback.isBlank()) 30.dp else 84.dp
                )
            )

            if(isToday) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AiTipBox(
                        message = workoutFeedback,
                        onRequestFeedback = {
                            // 피드백 요청 처리
                            goalViewModel.createHomeFeedBack(selectedDate.toString())
                        },
                        isLoading = isWorkLoading
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
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
                    onConfirm = { value ->
                        if (bodyLatestInfo?.bodyId != null) {
                            val request = BodyUpdateRequest(
                                bodyFat = value?.toDouble(),
                                measurementDate = selectedDate.toString()
                            )
                            weightViewModel.updateBodyInfo(bodyLatestInfo!!.bodyId!!, request)
                        } else {
                            val request = BodyRegisterRequest(
                                weight = userWeight!!.toDouble(),
                                bodyFat = bodyFat!!.toDouble(),
                                muscleMass = muscleMass!!.toDouble(),
                                height = userHeight!!.toDouble(),
                                measurementDate = selectedDate.toString()
                            )
                            weightViewModel.registerBodyData(request)
                        }

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
                    initialValue = muscleMass?.toString() ?: "",
                    onConfirm = { value ->
                        if (bodyLatestInfo?.bodyId != null) {
                            val request = BodyUpdateRequest(
                                muscleMass = value?.toDouble(),
                                measurementDate = selectedDate.toString()
                            )
                            weightViewModel.updateBodyInfo(bodyLatestInfo!!.bodyId!!, request)
                        } else {
                            val request = BodyRegisterRequest(
                                weight = userWeight!!.toDouble(),
                                bodyFat = bodyFat!!.toDouble(),
                                muscleMass = muscleMass!!.toDouble(),
                                height = userHeight!!.toDouble(),
                                measurementDate = selectedDate.toString()
                            )
                            weightViewModel.registerBodyData(request)
                        }

                        showMuscleSheet = false
                    },
                    onDismiss = { showMuscleSheet = false }
                )
            }
        }

        if (showHWSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHWSheet = false },
                containerColor = Color.White
            ) {
                BodyInfoEditSheet2Input(
                    heightInitValue = userHeight?.toString() ?: "",
                    weightInitValue = userWeight?.toString() ?: "",
                    onConfirm = { heightStr, weightStr ->
                        val height = heightStr.toDoubleOrNull()
                        val weight = weightStr.toDoubleOrNull()

                        if (height != null && weight != null) {
                            val request = BodyUpdateRequest(
                                height = height,
                                weight = weight,
                                measurementDate = selectedDate.toString()
                            )
                            if (bodyLatestInfo?.bodyId != null) {
                                weightViewModel.updateBodyInfo(bodyLatestInfo!!.bodyId!!, request)
                            } else {
                                val newRequest = BodyRegisterRequest(
                                    weight = weight,
                                    height = height,
                                    bodyFat = bodyFat!!,
                                    muscleMass = muscleMass!!,
                                    measurementDate = selectedDate.toString()
                                )
                                weightViewModel.registerBodyData(newRequest)
                            }
                        }

                        showHWSheet = false
                    },
                    onDismiss = { showHWSheet = false }
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
