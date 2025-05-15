package com.example.diaviseo.ui.detail

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.R
import com.example.diaviseo.network.meal.dto.res.MealTimeNutritionResponse
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.meal.MealCard
import com.example.diaviseo.ui.detail.components.meal.MealEmptyCard
import com.example.diaviseo.ui.detail.components.meal.MealSkippedCard
import com.example.diaviseo.ui.main.components.goal.meal.DonutChartWithLegend
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.DietSearchViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.MealViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.ZoneId

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MealDetailScreen(
    navController: NavHostController,
    dietViewModel: DietSearchViewModel
) {
    val context = LocalContext.current
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

    // 몇월 며칠 날짜 파싱
    val day = selectedDate.dayOfMonth.toString()

    // 해당날짜 식단기록, 부모랑 같이 써야함
    val mealViewModel: MealViewModel = viewModel(parentEntry)
    LaunchedEffect(selectedDate) {
        // 비동기 작업
        awaitAll(
            async { mealViewModel.fetchPhysicalInfo(selectedDate.toString()) },
            async { mealViewModel.fetchMealDaily(selectedDate.toString()) }
        )
        mealViewModel.fetchDailyNutrition(selectedDate.toString())
    }

    val dailyNutrition by mealViewModel.dailyNutrition.collectAsState()
    val nowPhysicalInfo by mealViewModel.nowPhysicalInfo.collectAsState()  // 이건 권장 소비량도 있음
    val mealDaily by mealViewModel.mealDaily.collectAsState()  // 이건 권장 소비량도 있음
    val carbRatio by mealViewModel.carbRatio.collectAsState()
    val sugarRatio by mealViewModel.sugarRatio.collectAsState()
    val proteinRatio by mealViewModel.proteinRatio.collectAsState()
    val fatRatio by mealViewModel.fatRatio.collectAsState()
    val mealLoading by mealViewModel.isLoading.collectAsState()

    val mealList = remember { mutableStateListOf<MealTimeNutritionResponse>().apply { addAll(
        mealDaily?.mealTimes ?: emptyList()
    ) } }

    // 2) mealDaily가 바뀔 때마다 mealList 갱신
    LaunchedEffect(mealDaily?.mealTimes) {
        mealList.clear()
        mealList.addAll(mealDaily?.mealTimes ?: emptyList())
    }

    LoadingOverlay(isVisible = isLoading || mealLoading)

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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DiaViseoColors.Callout)
                    .height(200.dp)
                    .padding(start = 24.dp, top = 40.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ){
                DonutChartWithLegend(
                    calories = dailyNutrition?.totalCalorie,
                    calorieGoal = nowPhysicalInfo?.recommendedIntake,
                    carbRatio = carbRatio,
                    sugarRatio = sugarRatio,
                    proteinRatio = proteinRatio,
                    fatRatio = fatRatio
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 시간대별 식단 카드 4개
            MealTimeType.entries.forEach { mealType ->
                val timeData = mealList.find { it.mealType == mealType.name }

                when {
                    timeData != null && timeData.foods.isNotEmpty() -> {
                        MealCard(
                            title = mealType.label,
                            kcal = timeData.nutrition.totalCalorie,
                            eatingTime = timeData.eatingTime,
                            nutrition = timeData.nutrition,
                            foods = timeData.foods,
                            gradient = mealType.gradient,
                            mealIconRes = mealType.iconRes,
                            onEditClick = { /* TODO */ },
                            imgUrl = timeData.mealTimeImageUrl
                        )
                    }
                    timeData != null && timeData.foods.isEmpty() -> {
                        MealSkippedCard(
                            title = mealType.label,
                            kcal = 0,
                            gradient = mealType.gradient,
                            mealIconRes = mealType.iconRes,
                            onEditClick = { /* TODO */ }
                        )
                    }
                    else -> {
                        MealEmptyCard(
                            title = mealType.label,
                            kcal = 0,
                            gradient = mealType.gradient,
                            mealIconRes = mealType.iconRes,
                            onSkippedClick = {
                                // dietsearch뷰모델에서 submitDiet 사용
                                // toPostDietRequest를 하기위해
                                // 뷰모델 내 selectDate 바꾸기, selectedDate는 여기 선택된 날짜로
                                dietViewModel.onDateSelected(selectedDate)
                                // 스킵한 시간은 클릭한 시간
                                val seoulTime: LocalTime = LocalTime.now(ZoneId.of("Asia/Seoul"))
                                dietViewModel.onTimeSelected(seoulTime)
                                // selectedItems는 emptyList()로
                                dietViewModel.skipSelectedItems()
                                // 선댁된 끼니는 mealType으로
                                dietViewModel.onMealSelected(mealType.label)

                                dietViewModel.submitDiet(
                                    context = context,
                                    imageUri = null,
                                    onSuccess = {
                                        Toast.makeText(context, "${mealType.label}을 거르셨어요", Toast.LENGTH_SHORT).show()

                                        dietViewModel.clearDietState()
                                    },
                                    onError = { message ->
                                        Toast.makeText(context, "거르기 등록 실패: $message", Toast.LENGTH_SHORT).show()
                                    }
                                )

                                // 식단 다시부르기
                                mealViewModel.fetchMealDaily(selectedDate.toString())
                            },
                            onWriteClick = {
                                // toPostDietRequest를 하기위해
                                // 뷰모델 내 selectDate 바꾸기, selectedDate는 여기 선택된 날짜로
                                dietViewModel.onDateSelected(selectedDate)
                                // 식단 시간은 비어서 보내자
                                dietViewModel.onTimeSelected(null)
                                // selectedItems는 emptyList()로
                                dietViewModel.skipSelectedItems()
                                // 선댁된 끼니는 mealType으로
                                dietViewModel.onMealSelected(mealType.label)


                                navController.navigate("diet_register")
                            }
                        )
                    }
                }
            }
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

enum class MealTimeType(
    val label: String,
    val gradient: Brush,
    val iconRes: Int
) {
    BREAKFAST("아침", Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color.White)), R.drawable.morning),
    LUNCH("점심", Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color.White)), R.drawable.lunch),
    DINNER("저녁", Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color.White)), R.drawable.night),
    SNACK("간식", Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color.White)), R.drawable.apple)
}