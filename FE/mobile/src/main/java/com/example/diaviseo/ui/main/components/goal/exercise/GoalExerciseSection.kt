package com.example.diaviseo.ui.main.components.goal.exercise

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.R
import com.example.diaviseo.network.exercise.dto.res.DayExerciseStatsResponse.ExerciseDetail
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.viewmodel.goal.ExerciseViewModel
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GoalExerciseSection(
    isToday: Boolean
) {
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val totalExCalories by exerciseViewModel.totalCalories.collectAsState()
    val totalExerciseTime by exerciseViewModel.totalExerciseTime.collectAsState()
    val exerciseCount by exerciseViewModel.exerciseCount.collectAsState()
    val exerciseEXList by exerciseViewModel.exerciseList.collectAsState()

    fun convertToExerciseItems(exerciseList: List<ExerciseDetail>): List<ExerciseItem> {
        return if (!exerciseList.isEmpty()) {
            exerciseList.map {
                ExerciseItem(
                    name = it.exerciseName,
                    kcal = it.exerciseCalorie,
                    min = it.exerciseTime,
                )
            }
        } else {
            listOf()
        }
    }

    // 데이터 변환
    var exerciseList by remember{mutableStateOf<List<ExerciseItem>>(listOf())}

    LaunchedEffect(exerciseEXList) {
        exerciseList = convertToExerciseItems(exerciseEXList)
    }

    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

//    val totalKcal = 259
//    val totalMin = 34
//    val exerciseList = listOf(
//        ExerciseItem(
//            name = "걷기",
//            kcal = 157,
//            min = 20,
//            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
//        ),
//        ExerciseItem(
//            name = "자전거 타기",
//            kcal = 157,
//            min = 20,
//            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
//        ),
//        ExerciseItem(
//            name = "자전거 타기",
//            kcal = 157,
//            min = 20,
//            imageUrl = "https://img.freepik.com/free-vector/human-sprint-icon-logo-design_474888-2493.jpg"
//        )
//    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isToday) "지금까지 소비한 칼로리는?" else "이 날의 소비한 칼로리는?",
            style = semibold16,
            color = DiaViseoColors.Basic
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${totalExCalories} kcal , ${totalExerciseTime}분",
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
                        color = Color(0xFF5C9DFF),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                if(!exerciseList.isEmpty()){
                    exerciseList.forEach {
                        ExerciseItemCard(it)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                } else {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "아직 등록된 운동이 없어요! 🎯",
                            style = semibold16,
                            color = Color.White.copy(alpha = animatedAlpha),
                        )
                    }
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
    val min: Int
)
