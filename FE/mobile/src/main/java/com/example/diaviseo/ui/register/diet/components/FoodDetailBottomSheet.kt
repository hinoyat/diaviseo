package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.diaviseo.R
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailBottomSheet(
    food: FoodDetailResponse,
    initialQuantity: Float = 1.0f,
    onToggleFavorite: () -> Unit,
    onAddClick: (quantity: Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var quantity by remember { mutableStateOf(initialQuantity) }
    var favoriteState by remember { mutableStateOf(food.isFavorite) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Black.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 제목 및 즐겨찾기
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = food.foodName, style = semibold20)
                IconButton(onClick = {
                    favoriteState = !favoriteState
                    onToggleFavorite()
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (favoriteState) R.drawable.favorite_star else R.drawable.unfavorite_star
                        ),
                        contentDescription = "즐겨찾기",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
//            Text("섭취량", style = semibold16)
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
//                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // 총 칼로리 + 인분 선택을 같은 줄에 배치
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 총 칼로리 (왼쪽)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = bold24.toSpanStyle()) {
                                append(String.format("%.1f", quantity * food.calorie))
                            }
                            append(" ")
                            withStyle(style = medium16.toSpanStyle()) {
                                append("kcal")
                            }
                        }
                    )

                    // 인분 선택 UI (오른쪽)
                    Row(
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 0.5f) quantity -= 0.5f },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "감소",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(modifier = Modifier.width(72.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (quantity % 1f == 0f)
                                    "${quantity.toInt()} 인분"
                                else
                                    "${String.format("%.1f", quantity)} 인분",
                                style = regular17
                            )
                        }

                        IconButton(
                            onClick = { quantity += 0.5f },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "증가",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text("영양 정보", style = semibold16)
            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow("열량", "${food.calorie} kcal")
                        InfoRow("단백질", "${food.protein} g")
                        InfoRow("당류", "${food.sweet} g")
                        InfoRow("콜레스테롤", "${food.cholesterol} mg")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow("탄수화물", "${food.carbohydrate} g")
                        InfoRow("지방", "${food.fat} g")
                        InfoRow("포화지방", "${food.saturatedFat} g")
                        InfoRow("트랜스지방", "${food.transFat} g")
                        InfoRow("나트륨", "${food.sodium} mg")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


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
                Text("추가하기", style = regular16)
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = regular14, color = Color.Gray)
        Text(text = value, style = medium14, color = Color.Black)
    }
}
