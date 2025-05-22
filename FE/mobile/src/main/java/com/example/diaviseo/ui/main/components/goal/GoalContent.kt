package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import java.time.LocalDate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
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
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.semibold16
import com.example.diaviseo.viewmodel.goal.ExerciseViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import com.example.diaviseo.viewmodel.goal.MealViewModel
import com.example.diaviseo.viewmodel.goal.WeightViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Composable
fun GoalContent(
    selectedTab: String,
    navController: NavHostController,
    gender: String?
) {
    // 평가<->디테일 날짜, 피드백 관리
    val goalViewModel: GoalViewModel = viewModel()
    val selectedDate by goalViewModel.selectedDate.collectAsState()
    val nutritionFeedback by goalViewModel.nutritionFeedback.collectAsState()
    val isNutriLoading by goalViewModel.isNutriLoading.collectAsState()
    val weightFeedback by goalViewModel.weightFeedback.collectAsState()
    val isWeightLoading by goalViewModel.isWeightLoading.collectAsState()


    // 평가<->디테일 식단 관리
    val mealViewModel: MealViewModel = viewModel()
    val dailyNutrition by mealViewModel.dailyNutrition.collectAsState()
    val nowPhysicalInfo by mealViewModel.nowPhysicalInfo.collectAsState()  // 이건 권장 소비량도 있음
    val carbRatio by mealViewModel.carbRatio.collectAsState()
    val sugarRatio by mealViewModel.sugarRatio.collectAsState()
    val proteinRatio by mealViewModel.proteinRatio.collectAsState()
    val fatRatio by mealViewModel.fatRatio.collectAsState()

    // 평가<->디테일 운동 관리
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val dailyData by exerciseViewModel.dailyStats.collectAsState()
    val weeklyData by exerciseViewModel.weeklyStats.collectAsState()
    val monthlyData by exerciseViewModel.monthlyStats.collectAsState()
    val stepData by exerciseViewModel.stepData.collectAsState()

    // 평가 체성분
    val weightViewModel : WeightViewModel = viewModel()
    val bodyInfo by weightViewModel.bodyInfo.collectAsState()
    val dayList by weightViewModel.dayList.collectAsState()
    val weekList by weightViewModel.weekList.collectAsState()
    val monthList by weightViewModel.monthList.collectAsState()

    LaunchedEffect(selectedDate) {
        weightViewModel.loadBodyData(selectedDate.toString())
        coroutineScope {
            goalViewModel.isThereFeedback("nutrition", selectedDate.toString())
            goalViewModel.isThereFeedback("weight_trend", selectedDate.toString())
        }
    }

    val isToday = remember(selectedDate) {
        selectedDate == LocalDate.now()
    }

    LaunchedEffect(selectedDate) {
        mealViewModel.fetchPhysicalInfo(selectedDate.toString())
        coroutineScope {
            mealViewModel.fetchMealStatistic("DAY", selectedDate.toString())
            exerciseViewModel.fetchAllStats(selectedDate.toString())
            mealViewModel.fetchDailyNutrition(selectedDate.toString())
            exerciseViewModel.fetchDailyExercise(selectedDate.toString())
            weightViewModel.fetchAllLists(selectedDate.toString())
        }
    }

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
                    calories = dailyNutrition?.totalCalorie ?: 0,
                    calorieGoal = nowPhysicalInfo?.recommendedIntake ?: 0,
                    carbRatio = carbRatio,
                    sugarRatio = sugarRatio,
                    proteinRatio = proteinRatio,
                    fatRatio = fatRatio
                )

                Spacer(
                    modifier = Modifier.height(
                        if (nutritionFeedback.isBlank()) 30.dp else 60.dp
                    )
                )

                if (dailyNutrition?.totalCalorie != 0) {
                    AiTipBox(
                        message = nutritionFeedback,
                        onRequestFeedback = {
                             goalViewModel.createNutriFeedBack(selectedDate.toString())
                        },
                        isLoading = isNutriLoading
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFD3EAFA), Color(0xFFC7D2FF))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 20.dp, horizontal = 24.dp)
                    ) {
                        Text(
                            text = "✨ AI 코멘트를 받고 싶으시다면 식단을 입력해주세요!",
                            style = medium16,
                            color = DiaViseoColors.Basic
                        )
                    }
                }

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

                LineChartSection(
                    dailyData = dailyData ?: emptyList(),
                    weeklyData = weeklyData ?: emptyList(),
                    monthlyData = monthlyData ?: emptyList()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "이번주, 난 얼마나 걸었을까?",
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
                Spacer(modifier = Modifier.height(24.dp))

                StepBarChart(stepData = stepData)
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
                    weight = if (bodyInfo != null) bodyInfo!!.weight else null,
                    muscleMass = if (bodyInfo != null) bodyInfo!!.muscleMass else null,
                    fatMass = if (bodyInfo != null) bodyInfo!!.bodyFat else null,
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (bodyInfo != null) {
                    BMRMBISection(
                        bmr = bodyInfo!!.bmr,
                        bmi = bodyInfo!!.bmi
                    )
                }

                Spacer(
                    modifier = Modifier.height(
                        if (weightFeedback.isBlank()) 30.dp else 80.dp
                    )
                )

                if ( bodyInfo != null && bodyInfo!!.weight != 0.0 && bodyInfo!!.muscleMass != 0.0 && bodyInfo!!.bodyFat != 0.0) {
                    AiTipBox(
                        message = weightFeedback,
                        onRequestFeedback = {
                            goalViewModel.createWeightFeedBack(selectedDate.toString())
                        },
                        isLoading = isWeightLoading
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFD3EAFA), Color(0xFFC7D2FF))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 20.dp, horizontal = 24.dp)
                    ) {
                        Text(
                            text = "✨ AI 코멘트를 받고 싶으시다면 체성분 정보를 모두 기입해주세요!",
                            style = medium16,
                            color = DiaViseoColors.Basic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                WeightChartSection(
                    dayList = dayList,
                    weekList = weekList,
                    monthList = monthList
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}