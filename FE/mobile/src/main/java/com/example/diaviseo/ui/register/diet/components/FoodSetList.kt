package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.network.foodset.dto.res.FoodSetResponse


@Composable
fun FoodSetList(
    foodSets: List<FoodSetResponse>,
    onSetClick: (FoodSetResponse) -> Unit = {},
    onAddSetClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 8.dp,
            bottom = 150.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(foodSets) { foodSet ->
            val foodList = foodSet.foods.map { it.foodName to it.calorie }

            FoodSetCard(
                title = foodSet.name,
                foodList = foodList,
                onClickRecord = { onSetClick(foodSet) }
            )
        }
        item {
            AddFoodSetCard(onClick = onAddSetClick)
        }
    }
}
