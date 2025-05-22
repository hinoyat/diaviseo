package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.network.food.dto.res.FoodItem
import com.example.diaviseo.viewmodel.DietSearchViewModel
import com.example.diaviseo.ui.register.diet.components.SearchSuggestionList

@Composable
fun FavoriteFoodsContent(
    viewModel: DietSearchViewModel,
    onFoodClick: (FoodItem) -> Unit
) {
    val favoriteFoods = viewModel.favoriteFoods
    val selectedItems = viewModel.selectedItems.map { it.foodId }

    if (favoriteFoods.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyStateView(
                tabType = EmptyTabType.FAVORITE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
            )
        }
    } else {
        SearchSuggestionList(
            results = favoriteFoods,
            selectedItems = selectedItems,
            onToggleSelect = { viewModel.onToggleSelect(it) },
            modifier = Modifier.fillMaxWidth(),
            onFoodClick = onFoodClick,
            )
    }
}
