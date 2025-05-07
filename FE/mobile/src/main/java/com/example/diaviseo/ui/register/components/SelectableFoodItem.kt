package com.example.diaviseo.ui.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*

data class FoodItemUiModel(
    val name: String,
    val quantity: String,
    val calorie: String,
    val isAdded: Boolean = false
)

@Composable
fun SelectableFoodItem(
    item: FoodItemUiModel,
    onToggle: (FoodItemUiModel) -> Unit,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 음식명 + 분량
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = item.name, style = medium16, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.quantity, style = regular14, color = Color.Gray)
            }

            // 칼로리
            Text(
                text = item.calorie,
                style = medium14,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            // + 또는 - 버튼
            IconButton(onClick = { onToggle(item) }) {
                Icon(
                    painter = painterResource(
                        id = if (item.isAdded) R.drawable.minus else R.drawable.plus
                    ),
                    contentDescription = if (item.isAdded) "제거" else "추가",
                    tint = DiaViseoColors.Main1
                )
            }
        }

        if (showDivider) {
            Divider(
                thickness = 1.dp,
                color = Color(0xFFF1F1F1),
//                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
