package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.semibold16

@Composable
fun DietRegisterBottomBar(
    selectedMeal: String,
    onMealClick: () -> Unit,
    selectedCount: Int,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = selectedCount > 0

    val buttonText = if (isActive) "${selectedCount}개 기록하기" else "기록하기"
    val buttonColor = if (isActive) DiaViseoColors.Main1 else Color(0xFFA8C7FF)

    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 🍽️ 식사 시간 선택 버튼
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3EDFF), shape = RoundedCornerShape(12.dp))
                    .clickable { onMealClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedMeal,
                        style = medium14,
                        color = Color(0xFF1A3E84)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "식사시간 선택",
                        tint = Color(0xFF1A3E84)
                    )
                }
            }

            // 📝 기록하기 버튼
            Button(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(12.dp),
                enabled = isActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(48.dp)
                    .width(150.dp)
            ) {
                Text(
                    text = buttonText,
                    style = semibold16
                )
            }
        }
    }
}
