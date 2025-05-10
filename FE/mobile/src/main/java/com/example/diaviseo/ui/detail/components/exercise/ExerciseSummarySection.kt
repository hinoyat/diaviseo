package com.example.diaviseo.ui.detail.components.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold24
import com.example.diaviseo.ui.theme.medium16

@Composable
fun ExerciseSummarySection(
    hasExercise: Boolean,
    totalMinutes: Int,
    totalKcal: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DiaViseoColors.Callout)
            .padding(vertical = 24.dp, horizontal = 42.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val imageRes = if (hasExercise) R.drawable.charac_exercise else R.drawable.charac_rest

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "운동 캐릭터",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(140.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = "총 운동 시간",
                style = medium16,
                color = DiaViseoColors.Basic
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$totalMinutes",
                    style = bold24,
                    color = DiaViseoColors.Basic
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "분",
                    style = medium16,
                    color = DiaViseoColors.Basic)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "총 소모량",
                style = medium16,
                color = DiaViseoColors.Basic
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$totalKcal",
                    style = bold24,
                    color = DiaViseoColors.Basic
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "kcal",
                    style = medium16,
                    color = DiaViseoColors.Basic
                )
            }
        }
    }
}