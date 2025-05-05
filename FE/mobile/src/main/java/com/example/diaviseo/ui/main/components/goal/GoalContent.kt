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
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import java.time.LocalDate
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.semibold16

@Composable
fun GoalContent(
    selectedTab: String,
    today: LocalDate
) {
    val isToday = remember(today) {
        today == LocalDate.now()
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
                    calories = 689,
                    calorieGoal = 1285,
                    carbRatio = 0.3f,
                    sugarRatio = 0.1f,
                    proteinRatio = 0.1f,
                    fatRatio = 0.2f
                )
                Spacer(modifier = Modifier.height(20.dp))

                AiTipBox("탄단지당 균형이 좋아요!\n지금처럼만 유지해요!")
                Spacer(modifier = Modifier.height(24.dp))

                MealChartSection()
                Spacer(modifier = Modifier.height(24.dp))

                MealBanner(
                    isToday = isToday,
                    onClick = { /* 상세화면 이동 예정 */ }
                )
            }

            "운동" -> {
                // TODO: 운동 리포트 컴포넌트 연결 예정
            }

            "체중" -> {
                // TODO: 체중 리포트 컴포넌트 연결 예정
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}