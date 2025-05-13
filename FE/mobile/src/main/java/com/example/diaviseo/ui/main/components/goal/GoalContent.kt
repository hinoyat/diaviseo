package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import java.time.LocalDate
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.main.components.goal.body.BMRMBISection
import com.example.diaviseo.ui.main.components.goal.body.WeightChartSection
import com.example.diaviseo.ui.main.components.goal.body.WeightOverviewSection
import com.example.diaviseo.ui.main.components.goal.exercise.GoalExerciseSection
import com.example.diaviseo.ui.main.components.goal.exercise.LineChartSection
import com.example.diaviseo.ui.main.components.goal.exercise.StepBarChart
import com.example.diaviseo.ui.main.components.goal.meal.DonutChartWithLegend
import com.example.diaviseo.ui.main.components.goal.meal.MealChartSection
import com.example.diaviseo.ui.theme.semibold16
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.MealViewModel
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun GoalContent(
    selectedTab: String,
    navController: NavHostController
) {
    // 평가<->디테일 날짜 관리
    val goalViewModel: GoalViewModel = viewModel()
    val selectedDate by goalViewModel.selectedDate.collectAsState()

    // 평가<->디테일 식단 관리
    val mealViewModel: MealViewModel = viewModel()
    val dailyNutrition by mealViewModel.dailyNutrition.collectAsState()
    val nowPhysicalInfo by mealViewModel.nowPhysicalInfo.collectAsState()
    val carbRatio by mealViewModel.carbRatio.collectAsState()
    val sugarRatio by mealViewModel.sugarRatio.collectAsState()
    val proteinRatio by mealViewModel.proteinRatio.collectAsState()
    val fatRatio by mealViewModel.fatRatio.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()

    val isToday = remember(selectedDate) {
        selectedDate == LocalDate.now()
    }
    val isMale = false

    LaunchedEffect(selectedDate) {
        // 순서 중요
        mealViewModel.fetchPhysicalInfo(selectedDate.toString())
        delay(100)
        mealViewModel.fetchDailyNutrition(selectedDate.toString())

        mealViewModel.fetchMealStatistic("DAY", selectedDate.toString())
    }

    LoadingOverlay(isLoading)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            "식단" -> {
                Text(
                    text = if (isToday) "지금까지 섭취한 칼로리는?" else "이 날의 섭취한 칼로리는?",
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
                Spacer(modifier = Modifier.height(40.dp))

                DonutChartWithLegend(
                    calories = dailyNutrition?.totalCalorie,
                    calorieGoal = nowPhysicalInfo?.recommendedIntake,
                    carbRatio = carbRatio,
                    sugarRatio = sugarRatio,
                    proteinRatio = proteinRatio,
                    fatRatio = fatRatio
                )
                Spacer(modifier = Modifier.height(40.dp))

                AiTipBox("탄단지당 균형이 좋아요!\n지금처럼만 유지해요!")
                Spacer(modifier = Modifier.height(24.dp))

                MealChartSection()
                Spacer(modifier = Modifier.height(24.dp))

                Banner(
                    isToday = isToday,
                    type = GoalBannerType.MEAL,
                    onClick = {
                        navController.navigate("meal_detail") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            "운동" -> {
                GoalExerciseSection(isToday = isToday)

                LineChartSection()
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "난 얼마나 걸었을까?",
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
                Spacer(modifier = Modifier.height(24.dp))

                StepBarChart()
                Spacer(modifier = Modifier.height(24.dp))

                Banner(
                    isToday = isToday,
                    type = GoalBannerType.EXERCISE,
                    onClick = {
                        navController.navigate("exercise_detail") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            "체중" -> {
                WeightOverviewSection(
                    isToday = isToday,
                    isMale = isMale,
                    weight = 59.1f,
                    muscleMass = 19.3f,
                    fatMass = 17.1f,
                )
                Spacer(modifier = Modifier.height(12.dp))

                BMRMBISection(
                    weight = 59.1f,
                    heightCm = 166f,
                    age = 26,
                    isMale = isMale
                )
                Spacer(modifier = Modifier.height(48.dp))

                AiTipBox("현재 체중 대비 골격근량은 꽤 좋은 편이라 근육량은 잘 유지되고 있어요. 다만 체지방률이 약간 높은 편일 수 있으니, 유산소 운동과 함께 단백질 섭취를 늘려 체지방을 천천히 줄이는 방향이 이상적이에요. 균형 잡힌 식단과 꾸준한 활동이 핵심이에요!")
                Spacer(modifier = Modifier.height(24.dp))

                WeightChartSection()
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}