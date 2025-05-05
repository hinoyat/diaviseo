package com.example.diaviseo.ui.main
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.main.components.home.AiAssistantCard
import com.example.diaviseo.ui.main.components.home.CaloriesGaugeSection
import com.example.diaviseo.ui.main.components.home.MainHeader
import com.example.diaviseo.ui.main.components.home.StepCountCard
import com.example.diaviseo.ui.main.components.home.SummaryCard
import com.example.diaviseo.ui.main.components.home.WeightPredictionSection

@Composable
fun HomeScreen(
    userNickname: String,
    navController: NavHostController
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

@Composable
fun SummaryCardSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "오늘 활동 칼로리",
            iconResId = com.example.diaviseo.R.drawable.main_exercise,
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