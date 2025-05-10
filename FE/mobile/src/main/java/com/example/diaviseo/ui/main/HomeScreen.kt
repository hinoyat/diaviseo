package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.R
import com.example.diaviseo.ui.main.components.home.AiAssistantCard
import com.example.diaviseo.ui.main.components.home.CaloriesGaugeSection
import com.example.diaviseo.ui.main.components.home.MainHeader
import com.example.diaviseo.ui.main.components.home.StepCountCard
import com.example.diaviseo.ui.main.components.home.SummaryCard
import com.example.diaviseo.ui.main.components.home.WeightPredictionSection
import com.example.diaviseo.viewmodel.HomeViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // 화면 뜨자마자 회원정보 불러오기
    val homeViewModel: HomeViewModel = viewModel()
    // 화면 진입 시 한 번 실행됨
    LaunchedEffect(Unit) {
        homeViewModel
    }

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
                calorieDifference = -50 // 예시 데이터
            )

            Spacer(modifier = Modifier.height(12.dp))

            CaloriesGaugeSection(
                consumedCalorie = 1080,
                remainingCalorie = 150,
                burnedCalorie = 180,
                extraBurned = 100,
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryCardSection(navController = navController)

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
fun SummaryCardSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "오늘 활동 칼로리",
            iconResId = R.drawable.main_exercise,
            current = 96,   // 예시
            goal = 256,   // 예시
            goalExceeded = false,
            destinationRoute = "exercise_detail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "오늘 섭취 칼로리",
            iconResId = R.drawable.main_diet,
            current = 1796,   // 예시
            goal = 1533,   // 예시
            goalExceeded = 1796 > 1533,
            destinationRoute = "exercise_detail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )
    }
}