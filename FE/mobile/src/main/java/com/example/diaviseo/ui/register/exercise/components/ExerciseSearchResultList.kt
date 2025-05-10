package com.example.diaviseo.ui.register.exercise.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.model.exercise.Exercise
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.regular12
import com.example.diaviseo.ui.components.CircleAddButton

@Composable
fun ExerciseSearchResultList(
    exercise: List<Exercise>,
    onRegisterClick: (Exercise) -> Unit = {} // TODO 등록 로직 연결
){
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ){
        items(exercise) { exercise ->
            ExerciseSearchResultItem(exercise = exercise,onRegisterClick=onRegisterClick)
        }
    }
}

@Composable
fun ExerciseSearchResultItem(
    exercise: Exercise,
    onRegisterClick: (Exercise) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 🔘 원형 아이콘 자리 (임시) TODO 픽토그램 추가
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color(0xFFDDE3FD), shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = exercise.name,
                        style = medium16
                    )
                    Text(
                        text = exercise.category,
                        style = regular12,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            CircleAddButton(onClick = { onRegisterClick(exercise) })

        }

        Divider(
            modifier = Modifier.padding(start = 4.dp),
            thickness = 0.8.dp,
            color = Color(0xFFE0E0E0)
        )
    }
}
