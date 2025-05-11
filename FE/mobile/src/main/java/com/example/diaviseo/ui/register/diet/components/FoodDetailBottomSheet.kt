package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailBottomSheet(
    food: FoodDetailResponse,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAddClick: (quantity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var quantity by remember { mutableStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // 제목 및 즐겨찾기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = food.foodName, style = semibold20, color = Color.Black)
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (food.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "즐겨찾기",
                        tint = if (food.isFavorite) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 영양 정보
            InfoRow("열량", "${food.calorie} kcal")
            InfoRow("탄수화물", "${food.carbohydrate} g")
            InfoRow("단백질", "${food.protein} g")
            InfoRow("지방", "${food.fat} g")
            InfoRow("당류", "${food.sweet} g")
            InfoRow("나트륨", "${food.sodium} mg")
            InfoRow("포화지방", "${food.saturatedFat} g")
            InfoRow("트랜스지방", "${food.transFat} g")
            InfoRow("콜레스테롤", "${food.cholesterol} mg")

            Spacer(modifier = Modifier.height(20.dp))

            // 인분 선택
            Text("섭취한 인분 수", style = semibold16)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = { if (quantity > 1) quantity-- }) {
                    Text("-")
                }
                Text("$quantity 인분", style = bold16)
                OutlinedButton(onClick = { quantity++ }) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 추가하기 버튼
            Button(
                onClick = {
                    onAddClick(quantity)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiaViseoColors.Main1,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("추가하기", style = semibold16)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = regular14, color = Color.Gray)
        Text(text = value, style = medium14, color = Color.Black)
    }
}
