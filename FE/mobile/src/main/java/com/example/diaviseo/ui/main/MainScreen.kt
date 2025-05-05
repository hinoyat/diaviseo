package com.example.diaviseo.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.BottomNavigationBar
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import com.example.diaviseo.ui.main.components.*
import com.example.diaviseo.R

@Composable
fun MainScreen(navController: NavHostController) {
    // ✅ 외부에서 전달받는 navController 사용 (하단바 & 각 섹션에 라우팅 전달 목적)

    // 화면 이동을 관리해주는 내비게이션 컨트롤러
//    val navController = rememberNavController()
    val userNickname = "김디아" // TODO: ViewModel 등에서 유저 정보 주입
    val isFabMenuOpen = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController, // ✅ 바텀바에서 화면 이동 처리
                isFabMenuOpen = isFabMenuOpen
            )
        },
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
                        bottom = 360.dp
                    )
                )
        ) {
            MainHeader(userNickname, navController)

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

    // 조건부 UI는 Scaffold 바깥에!
    // 하단바 + 버튼 토글 (컴포넌트로 분리)
    if (isFabMenuOpen.value) {
        FabOverlayMenu(
            onDismiss = { isFabMenuOpen.value = false },
            navController = navController
        )
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
            destinationRoute = "exerciseDetail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "오늘 섭취 칼로리",
            iconResId = R.drawable.main_diet,
            current = 1796,   // 예시
            goal = 1533,   // 예시
            goalExceeded = 1796 > 1533,
            destinationRoute = "dietDetail",
            navController = navController,
            modifier = Modifier.weight(1f)
        )
    }
}