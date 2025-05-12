package com.example.diaviseo.ui.main.components.goal.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*

@Composable
fun GoalExerciseSection(
    isToday: Boolean
) {
    val totalKcal = 259
    val totalMin = 34
    val exerciseList = listOf(
        ExerciseItem(
            name = "걷기",
            kcal = 157,
            min = 20,
            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
        ),
        ExerciseItem(
            name = "자전거 타기",
            kcal = 157,
            min = 20,
            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
        ),
        ExerciseItem(
            name = "자전거 타기",
            kcal = 157,
            min = 20,
            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isToday) "지금까지 소비한 칼로리는?" else "이 날의 소비한 칼로리는?",
            style = semibold16,
            color = DiaViseoColors.Basic
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${totalKcal} kcal , ${totalMin}분",
            style = bold20,
            color = DiaViseoColors.Unimportant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            // 배경 박스
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = DiaViseoColors.Main1,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                exerciseList.forEach {
                    ExerciseItemCard(it)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // 캐릭터 이미지 (오른쪽 위에 걸쳐지게)
            Image(
                painter = painterResource(id = R.drawable.charac_exercise),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-86).dp)
                    .size(96.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이후 차트 영역, 걸음수, 배너 등 추가 예정
    }
}

data class ExerciseItem(
    val name: String,
    val kcal: Int,
    val min: Int,
    val imageUrl: String
)
