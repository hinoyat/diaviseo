package com.example.diaviseo.ui.register.exercise.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.diaviseo.model.exercise.Exercise
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.*

@Composable
fun ExerciseListSection(
    title: String,
    exercises: List<Exercise>,
    onRegisterClick:(Exercise) -> Unit
){
    if (exercises.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text("운동 기록이 없어요!", style = medium14, color=Color.Gray)
        }
    } else {
        ExerciseSearchResultList(
            exercise = exercises,
            onRegisterClick = onRegisterClick
        )
    }
}