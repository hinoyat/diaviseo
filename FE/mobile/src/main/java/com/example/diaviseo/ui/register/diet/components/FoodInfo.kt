package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.bold17
import com.example.diaviseo.ui.theme.medium17

@Composable
fun FoodInfoSection(
    foodName: String?= null,
    totalCalorie: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!foodName.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "음식 이름", style = bold17, color = Color.Black)
                Text(text = foodName, style = medium17, color = Color.Black)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "총 열량", style = bold17, color = Color.Black)
            Text(text = "$totalCalorie kcal", style = medium17, color = Color.Black)
        }
    }
}
