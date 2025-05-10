package com.example.diaviseo.ui.mypageedit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors

// 🔹 더미용 Exercise 모델 (임시)
data class Exercise(
    val id: Int,
    val name: String,
    val iconRes: Int,
    val categoryId: Int
)

@Composable
fun SelectableExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = exercise.iconRes),
                contentDescription = exercise.name,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                color = DiaViseoColors.Basic
            )
        }

        Icon(
            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
            contentDescription = if (isSelected) "선택됨" else "추가",
            tint = if (isSelected) DiaViseoColors.Main1 else DiaViseoColors.Middle,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectableExerciseItemPreview() {
    SelectableExerciseItem(
        exercise = Exercise(1, "걷기", R.drawable.charac_exercise, categoryId = 2),
        isSelected = false,
        onClick = {}
    )
}
