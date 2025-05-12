package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.exercise.AddExerciseBox
import com.example.diaviseo.ui.components.DeleteDialog
import com.example.diaviseo.ui.detail.components.exercise.ExerciseRecordItem
import com.example.diaviseo.ui.detail.components.exercise.ExerciseSummarySection
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold20
//import com.example.diaviseo.viewmodel.ExerciseDetailViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ExerciseDetailScreen(
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

    // 분기해서 viewModel() 호출
    val goalViewModel: GoalViewModel = if (parentEntry.destination.route == "goal") {
        // 이미 goal에서 왔을 땐, goal 백스택 엔트리로부터 같은 ViewModel 공유
        viewModel(parentEntry)
    } else {
        // home에서 왔을 땐, 이 화면(exercise_detail)의 엔트리로 새로운 ViewModel 생성
        viewModel()
    }

    val showDatePicker by goalViewModel.showDatePicker.collectAsState()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    val isLoading by goalViewModel.isLoading.collectAsState()
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    // 닉네임 가져오게 viewModel : profileviewmodel
    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

    // 몇월 며칠 날짜 파싱
    val day = selectedDate.dayOfMonth.toString()

    // 더미데이터
    val dummyExercises = remember {
        mutableStateListOf(
            ExerciseRecord("걷기", 157, 20, "2025-05-06T08:30:00"),
            ExerciseRecord("자전거 타기", 157, 20, "2025-05-06T08:30:00"),
            ExerciseRecord("자전거 타기", 157, 20, "2025-05-06T08:30:00"),
            ExerciseRecord("자전거 타기", 157, 20, "2025-05-06T08:30:00"),
            ExerciseRecord("자전거 타기", 157, 20, "2025-05-06T08:30:00"),
            ExerciseRecord("자전거 타기", 157, 20, "2025-05-06T08:30:00")
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDeleteIndex by remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "${selectedDate.monthValue}월 ${day}일 운동",
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
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 운동 요약 섹션
            ExerciseSummarySection(
                hasExercise = dummyExercises.isNotEmpty(),
                totalMinutes = 34,
                totalKcal = 259
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 유저 닉네임
            Text(
                text = "$nickname 님의 운동기록",
                style = bold20,
                color = DiaViseoColors.Basic,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. 운동 기록 목록
            dummyExercises.forEachIndexed { index, item ->
                ExerciseRecordItem(
                    title = item.title,
                    kcal = item.kcal,
                    time = item.time,
                    exerciseDate = item.exerciseDate,
                    onEditClick = {
                        // TODO: 바텀시트 호출
                    },
                    onDeleteClick = {
                        selectedDeleteIndex = index
                        showDeleteDialog = true
                    }
                )
            }

            // 4. 운동 더 추가하기 버튼
            AddExerciseBox(
                onClick = {
                    // TODO: 운동 등록 화면으로 이동
                }
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

        // 삭제 확인 다이얼로그
        if (showDeleteDialog) {
            DeleteDialog(
                title = "운동삭제 확인",
                content = "정말 삭제하시겠습니까?",
                onConfirm = {
                    dummyExercises.removeAt(selectedDeleteIndex)
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}


data class ExerciseRecord(
    val title: String,
    val kcal: Int,
    val time: Int,
    val exerciseDate: String,
)