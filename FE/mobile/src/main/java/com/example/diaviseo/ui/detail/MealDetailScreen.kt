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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.DiaDatePickerDialog
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.detail.components.meal.MealCard
import com.example.diaviseo.ui.main.components.goal.DonutChartWithLegend
import com.example.diaviseo.viewmodel.GoalViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import com.example.diaviseo.network.meal.dto.res.MealFoodResponse
import com.example.diaviseo.network.meal.dto.res.MealNutritionResponse

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
    LoadingOverlay(isVisible = goalViewModel.isLoading.collectAsState().value)

    // 닉네임 가져오게 viewModel : profileviewmodel
    val myProfile by viewModel.myProfile.collectAsState()
    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

    // 몇월 며칠 날짜 파싱
    val day = selectedDate.dayOfMonth.toString()

    val dummyBrush = Brush.linearGradient(
        0.0f to Color.Red,
        0.3f to Color.Green,
        1.0f to Color.Blue,
        start = Offset(0.0f, 50.0f),
        end = Offset(0.0f, 100.0f)
    )

    val dummyNutrition = MealNutritionResponse(
        totalCalorie = 910,
        totalCarbohydrate = 128.00,
        totalProtein = 72.80,
        totalFat = 11.60,
        totalSugar = 0.60,
        totalSodium = 150.00
    )

    val dummyFood: List<MealFoodResponse> = listOf(
        MealFoodResponse(
            mealFoodId = 10,
            foodId = 2,
            foodName = "닭가슴살",
            calorie = 165,
            carbohydrate = 0.00,
            protein = 31.00,
            fat = 3.60,
            sugar = 0.00,
            sodium = 70.00,
            quantity = 2,
            foodImageUrl = "https://example.com/image1.jpg",
            totalCalorie = 330,
            totalCarbohydrate = 0.00,
            totalProtein = 62.00,
            totalFat = 7.20,
            totalSugar = 0.00,
            totalSodium = 140.00
        ),
        MealFoodResponse(
            mealFoodId = 11,
            foodId = 1,
            foodName = "현미밥",
            calorie = 290,
            carbohydrate = 64.00,
            protein = 5.40,
            fat = 2.20,
            sugar = 0.30,
            sodium = 5.00,
            quantity = 2,
            foodImageUrl = "https://example.com/image1.jpg",
            totalCalorie = 580,
            totalCarbohydrate = 128.00,
            totalProtein = 10.80,
            totalFat = 4.40,
            totalSugar = 0.60,
            totalSodium = 10.00
        )
    )

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

            MealCard(
                title = "아침",
                kcal = 330,
                eatingTime = "08:30:00",
                nutrition = dummyNutrition,
                foods = dummyFood,
                gradient = dummyBrush,
                mealIconRes = "BREAKFAST",
                onEditClick = {}
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