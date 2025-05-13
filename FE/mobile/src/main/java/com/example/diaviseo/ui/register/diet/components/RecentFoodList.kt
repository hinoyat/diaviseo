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
import com.example.diaviseo.mapper.toFoodItem
import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse
import com.example.diaviseo.ui.components.AddRemoveIconButton

@Composable
fun RecentFoodList(
    foods: List<RecentFoodItemResponse>,
    selectedItems: List<Int>,
    fetchedDate: String?,
    onToggleSelect: (FoodItem) -> Unit,
    onFoodClick: (FoodItem) -> Unit
) {
    val mappedFoods = foods.map { it.toFoodItem() }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (mappedFoods.isEmpty()) {
            EmptyStateView(tabType = EmptyTabType.RECENT_FOOD)
        } else {
            fetchedDate?.let {
                Text(
                    text = "${it.replace("-", ".")} 기준",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
            }

            SearchSuggestionList(
                results = mappedFoods,
                selectedItems = selectedItems,
                onToggleSelect = onToggleSelect,
                onFoodClick = onFoodClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
