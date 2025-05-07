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
    val isMale = false

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

                Banner(
                    isToday = isToday,
                    type = GoalBannerType.MEAL,
                    onClick = { /* 상세화면 이동 예정 */ }
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
                        // TODO: 해당 날짜의 운동 상세 화면으로 이동
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