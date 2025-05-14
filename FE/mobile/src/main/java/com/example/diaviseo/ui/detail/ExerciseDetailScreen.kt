package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.model.exercise.ExerciseData
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.exercise.AddExerciseBox
import com.example.diaviseo.ui.components.DeleteDialog
import com.example.diaviseo.ui.detail.components.exercise.ExerciseRecordItem
import com.example.diaviseo.ui.detail.components.exercise.ExerciseSummarySection
import com.example.diaviseo.ui.register.exercise.components.ExerciseRegisterBottomSheet
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold20
//import com.example.diaviseo.viewmodel.ExerciseDetailViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel
import com.example.diaviseo.viewmodel.goal.ExerciseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import com.example.diaviseo.viewmodel.register.exercise.ExerciseRecordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ExerciseDetailScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
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

    // 닉네임 가져오게 viewModel : profileviewmodel
    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

    // 몇월 며칠 날짜 파싱
    val day = selectedDate.dayOfMonth.toString()

    // 해당날짜 운동기록, 부모랑 같이 써야함
    val exerciseViewModel: ExerciseViewModel = viewModel(parentEntry)
    LaunchedEffect(selectedDate) {
        exerciseViewModel.fetchDailyExercise(selectedDate.toString())
    }

    val totalExCalories by exerciseViewModel.totalCalories.collectAsState()
    val totalExerciseTime by exerciseViewModel.totalExerciseTime.collectAsState()
    val exerciseEXList by exerciseViewModel.exerciseList.collectAsState()
    val exerciseLoading by exerciseViewModel.isLoading.collectAsState()

    var exerciseList = remember { mutableStateListOf(*exerciseEXList.toTypedArray())}

    LaunchedEffect(exerciseEXList) {
        exerciseList.clear()
        exerciseList.addAll(exerciseEXList)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDeleteIndex by remember { mutableStateOf(-1) }

    // 바텀시트 상태
    val selectedExercise = remember { mutableStateOf<Exercise?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val exerciseRecordViewModel : ExerciseRecordViewModel = viewModel()
    val startTime by exerciseRecordViewModel.startTime.collectAsState()

    val (hourStr, minuteStr) = startTime.split(":")
    // 시간을 Int로 변환하여 조건 확인
    val hourInt = hourStr.toInt()

    // 시간이 13 이상이면 12를 빼기
    val adjustedHourInt = if (hourInt >= 13) hourInt - 12 else hourInt

    // 조정된 시간을 다시 문자열로 변환 (필요시 앞에 0 추가)
    val adjustedHourStr = adjustedHourInt.toString().padStart(2, '0')

    LoadingOverlay(isVisible = isLoading || exerciseLoading)

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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 운동 요약 섹션
            ExerciseSummarySection(
                hasExercise = exerciseEXList.isNotEmpty(),
                totalMinutes = totalExerciseTime,
                totalKcal = totalExCalories
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
            exerciseList.forEachIndexed { index, item ->
                ExerciseRecordItem(
                    title = item.exerciseName,
                    kcal = item.exerciseCalorie,
                    time = item.exerciseTime,
                    exerciseDate = item.exerciseDate,
                    onEditClick = {
                        // abc라고 해둔 거기에 selectedExercise 넣고 time도 넣고 시작 시간도 넣고 등록 날짜도 넣고
                        val matchedExercise = ExerciseData.exerciseList.find { it.name == item.exerciseName }
                        matchedExercise?.let {
                            exerciseRecordViewModel.setExercise(it)
                            selectedExercise.value = it
                        }
                        exerciseRecordViewModel.setExerciseId(item.exerciseId)
                        exerciseRecordViewModel.setExerciseTime(item.exerciseTime)
                        exerciseRecordViewModel.setRegisterDate(item.exerciseDate.substringBefore('T'))
                        exerciseRecordViewModel.setPutStartTime(item.exerciseDate.substringAfter('T').substringBeforeLast(':'))

                        // TODO: 바텀시트 호출
                        coroutineScope.launch {
                            sheetState.show()
                        }
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
                    navController.navigate("exercise_register/$selectedDate")
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
                    exerciseViewModel.deleteExercise(exerciseList[selectedDeleteIndex].exerciseId)
                    exerciseViewModel.setTotalCalories(-exerciseList[selectedDeleteIndex].exerciseCalorie)
                    exerciseViewModel.setTotalExerciseTime(-exerciseList[selectedDeleteIndex].exerciseTime)
                    exerciseList.removeAt(selectedDeleteIndex)
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }

    // ✅ 운동 등록 바텀시트
    selectedExercise.value?.let {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                selectedExercise.value = null
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            // ExerciseRegisterBottomSheet 파라미터에 수정이라는 걸 bool로 알리자
            ExerciseRegisterBottomSheet(
                viewModel = exerciseRecordViewModel,
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedExercise.value = null
                },
                onSuccess = {
                    // 확인 누르면 코루틴 비동기로 응답올때까지 기다렸다가 바텀시트 내리기
                    // 성공하자마자 fetchDailyExercise 부르고 바텀시트 내리기
                    coroutineScope.launch {
                        sheetState.hide()
                        exerciseViewModel.fetchDailyExercise(selectedDate.toString())
                    }
                    Log.d("ExerciseDetailScreen", "운동 수정 성공")
                },
                parmHour = adjustedHourStr,
                parmMinute = minuteStr,
                parmPeriod = if (hourInt < 12) "오전" else "오후",
                isPut = true
            )
        }
    }
}