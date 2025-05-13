package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse
import com.example.diaviseo.ui.components.AddRemoveIconButton

@Composable
fun RecentFoodList(
    foods: List<RecentFoodItemResponse>,
    selectedItems: List<Int>,
    fetchedDate: String?,
    onToggleSelect: (RecentFoodItemResponse) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = Color.White
    ) {
        if (foods.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyStateView(tabType = EmptyTabType.RECENT_FOOD)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 1.dp, vertical = 12.dp)
            ) {
                fetchedDate?.let {
                    Text(
                        text = "${it.replace("-", ".")} 기준",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                foods.forEachIndexed { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.foodName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1인분 (${item.baseAmount.filter { it.isDigit() }.toIntOrNull() ?: 0}g)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${item.calorie}kcal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                AddRemoveIconButton(
                                    isSelected = selectedItems.contains(item.foodId.toInt()),
                                    onClick = { onToggleSelect(item) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF0F0F0),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}
