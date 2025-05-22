package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.ui.components.AddRemoveIconButton

@Composable
fun SearchSuggestionList(
    results: List<FoodItem>,
    selectedItems: List<Int>,
    onToggleSelect: (FoodItem) -> Unit,
    modifier: Modifier = Modifier,
    onFoodClick: (FoodItem) -> Unit,
    ) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        color = Color.White
    ) {
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "검색 결과가 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                itemsIndexed(results) { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onFoodClick(item) }

                            ) {
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
                                    isSelected = selectedItems.contains(item.foodId),
                                    onClick = { onToggleSelect(item) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // 마지막 아이템이 아닌 경우만 Divider 표시
                        if (index != results.lastIndex) {
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF0F0F0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}
