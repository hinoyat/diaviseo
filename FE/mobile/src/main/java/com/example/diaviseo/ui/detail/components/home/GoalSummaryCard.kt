package com.example.diaviseo.ui.detail.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*

@Composable
fun GoalSummaryCard(
    nickname: String,
    goal: String,
    recommendedIntake: Int,
    recommendedExercise: Int,
    totalCalorie: Int,
    tdee: Int,
    totalExerciseCalorie: Int,
    predictValue: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 타이틀에 핀 아이콘 추가
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "📍 $nickname 님의 목표 : ${
                    when (goal) {
                        "WEIGHT_LOSS"       -> "체중 감량"
                        "WEIGHT_GAIN"       -> "체중 증량"
                        "WEIGHT_MAINTENANCE"-> "체중 유지"
                        else                -> goal
                    }
                }",
                style = bold20,
                color = DiaViseoColors.Basic
            )
        }

        // 2. 권장 섭취/소비 칼로리 박스 (pill 형태, wrapContent)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            RecommendationPill(label = "권장 섭취칼로리", value = recommendedIntake)
            Spacer(modifier = Modifier.height(8.dp))
            RecommendationPill(label = "권장 소비칼로리", value = recommendedExercise)
        }

        // 설명 텍스트
        Text(
            text = when (goal) {
                "WEIGHT_LOSS" -> "식단으로 $recommendedIntake kcal 제한, 운동으로 $recommendedExercise kcal 소모하면 적정몸무게로 감량할 수 있어요."
                "WEIGHT_GAIN" -> "식단으로 $recommendedIntake kcal 섭취, $recommendedExercise kcal 정도 운동하면 적정몸무게로 증량할 수 있어요."
                "WEIGHT_MAINTENANCE" -> "식단으로 $recommendedIntake kcal 섭취, $recommendedExercise kcal 정도 꾸준히 운동하면 적정몸무게를 유지할 수 있어요."
                else -> ""
            },
            style = medium14,
            color = DiaViseoColors.Basic
        )

        // 예측 추이 제목
        Text(
            text = "🌟 섭취, 칼로리로 예측하는 몸무게 추이",
            style = bold18,
            color = DiaViseoColors.Basic
        )

        // 3. 예측 박스 (kg 단위 제거, 앞뒤 공백 포함)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DiaViseoColors.Callout),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "섭취 칼로리 - TDEE - 운동 소모 칼로리",
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
                Text(
                    text = buildAnnotatedString {
                        append("= $totalCalorie - $tdee - $totalExerciseCalorie = ")
                        pushStyle(SpanStyle(color = DiaViseoColors.Main1))
                        append(predictValue.toString())
                        pop()
                    },
                    style = semibold16,
                    color = DiaViseoColors.Basic
                )
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.info),
                        contentDescription = "info",
                        modifier = Modifier.size(20.dp),
                        tint = DiaViseoColors.Unimportant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TDEE?\n 하루 총 에너지 소비량, 기초대사량에 신체 활동 수준을 곱한 것 입니다. 값으로 1.375를 사용합니다.",
                        style = medium12,
                        color = DiaViseoColors.Unimportant
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationPill(
    label: String,
    value: Int
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .width(290.dp)
            .height(32.dp)
            .border(
                BorderStroke(1.dp, DiaViseoColors.Placeholder),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = label, style = semibold14, color = DiaViseoColors.Basic, textAlign = TextAlign.Center)
        Spacer(Modifier.width(6.dp))
        Text(text = "$value kcal", style = semibold14, color = DiaViseoColors.Basic)
    }
}