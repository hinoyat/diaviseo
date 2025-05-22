package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.diaviseo.mapper.toFoodItem
import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun RecentFoodList(
    foods: List<RecentFoodItemResponse>,
    selectedItems: List<Int>,
    fetchedDate: String?,
    onToggleSelect: (FoodItem) -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    nickname: String
) {
    val mappedFoods = foods.map { it.toFoodItem() }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (mappedFoods.isEmpty()) {
            EmptyStateView(tabType = EmptyTabType.RECENT_FOOD)
        } else {
            fetchedDate?.let {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$nickname 님이 최근 드신 음식 목록",
                    style = bold17,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 3.dp, bottom = 4.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${it.replace("-", ".")} 기준",
                    style = regular14,
                    color = DiaViseoColors.Placeholder,
                    modifier = Modifier
                        .padding(start = 3.dp, bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            SearchSuggestionList(
                results = mappedFoods,
                selectedItems = selectedItems,
                onToggleSelect = onToggleSelect,
                onFoodClick = onFoodClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
//            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
