package com.example.diaviseo.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.BottomNavigationBar
import com.example.diaviseo.ui.main.components.*

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val userNickname = "김디아" // TODO: ViewModel 등에서 유저 정보 주입

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = Color(0xFFDFE9FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
//                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = innerPadding.calculateTopPadding() + 36.dp,
                        end = 16.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
        ) {
            MainHeader(userNickname, navController)

            WeightPredictionSection(
                calorieDifference = -50 // 예시 데이터
            )

            CaloriesGaugeSection(
                consumedCalorie = 1080,
                remainingCalorie = 150,
                burnedCalorie = 180,
                extraBurned = 100,
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))

//            SummaryCardSection(navController = navController)

//            AiAssistantCard(navController = navController)

//            StepCountCard(
//                stepsToday = 6118,
//                stepsYesterday = 5115
//            )

//            BloodSugarCard(
//                latestSugar = 89,
//                navController = navController
//            )
        }
    }
}