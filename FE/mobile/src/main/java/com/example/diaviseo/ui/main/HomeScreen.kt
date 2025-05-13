package com.example.diaviseo.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.LoadingOverlay
import com.example.diaviseo.ui.main.components.home.AiAssistantCard
import com.example.diaviseo.ui.main.components.home.CaloriesGaugeSection
import com.example.diaviseo.ui.main.components.home.MainHeader
import com.example.diaviseo.ui.main.components.home.StepCountCard
import com.example.diaviseo.ui.main.components.home.SummaryCard
import com.example.diaviseo.ui.main.components.home.WeightPredictionSection
import com.example.diaviseo.viewmodel.HomeViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel
) {
    val homeViewModel: HomeViewModel = viewModel()

    // 현재 이 composable의 BackStackEntry를 State로 구독
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // 그 엔트리에서 route만 꺼내기
    val currentRoute: String? = navBackStackEntry?.destination?.route

    // route가 이 화면일 때만 호출
    val today = remember { LocalDate.now() }
    LaunchedEffect(currentRoute) {
        if (currentRoute == "home") {
            homeViewModel.fetchDailyNutrition(today.toString())
            homeViewModel.fetchDailyExercise(today.toString())
        }
    }

    val recommendedEat by viewModel.recommendedEat.collectAsState()
    val recommendedFit by viewModel.recommendedFit.collectAsState()
    val tdee by viewModel.tdee.collectAsState()

    val totalCalorie by homeViewModel.totalCalorie.collectAsState()
    val totalExerciseCalorie by homeViewModel.totalExerciseCalorie.collectAsState()

    val remainingCalorie = recommendedEat - totalCalorie
    val extraBurned = recommendedFit - totalExerciseCalorie
    val calorieDifference = totalCalorie - tdee - totalExerciseCalorie

    LoadingOverlay(isVisible = homeViewModel.isLoading.collectAsState().value)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDFE9FF))  // ← 화면 전체 배경색
    ) {
        Column(
            modifier = Modifier
    //                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = 36.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )
        ) {
            MainHeader(viewModel= viewModel, navController= navController)

            Spacer(modifier = Modifier.height(24.dp))

            WeightPredictionSection(
                calorieDifference = calorieDifference // 예시 데이터
            )

            Spacer(modifier = Modifier.height(12.dp))

            CaloriesGaugeSection(
                recommendedEat =recommendedEat,
                recommendedFit = recommendedFit,
                consumedCalorie = totalCalorie,
                remainingCalorie = remainingCalorie,
                burnedCalorie = totalExerciseCalorie,
                extraBurned = extraBurned,
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryCardSection(
                navController = navController,
                viewModel = viewModel,
                homeViewModel = homeViewModel
            )

            Spacer(modifier = Modifier.height(12.dp))

            AiAssistantCard(navController = navController)

            Spacer(modifier = Modifier.height(12.dp))

            StepCountCard(
    //                stepsToday = 6118,
                yesterdaySteps = 5115
            )

    //            BloodSugarCard(
    //                latestSugar = 89,
    //                navController = navController
    //            )
        }
    }
}

@Composable
fun SummaryCardSection(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    homeViewModel: HomeViewModel
) {
    val recommendedEat by viewModel.recommendedEat.collectAsState()
    val recommendedFit by viewModel.recommendedFit.collectAsState()

    val totalCalorie by homeViewModel.totalCalorie.collectAsState()
    val totalExerciseCalorie by homeViewModel.totalExerciseCalorie.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "오늘 활동 칼로리",
            iconResId = R.drawable.main_exercise,
            current = totalExerciseCalorie,
            goal = recommendedFit,
            goalExceeded = false,
            destinationRoute = "exercise_detail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "오늘 섭취 칼로리",
            iconResId = R.drawable.main_diet,
            current = totalCalorie,
            goal = recommendedEat,
            goalExceeded = totalCalorie > recommendedEat,
            destinationRoute = "meal_detail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )
    }
}