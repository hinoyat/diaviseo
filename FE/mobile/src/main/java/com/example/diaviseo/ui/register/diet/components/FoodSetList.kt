package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaviseo.network.foodset.dto.res.FoodSetResponse
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.medium13

@Composable
fun FoodSetList(
    foodSets: List<FoodSetResponse>,
    onSetClick: (FoodSetResponse) -> Unit = {},
    onAddSetClick: () -> Unit = {}
) {
    if (foodSets.isEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // EmptyStateView 포함
                EmptyStateView(tabType = EmptyTabType.FOOD_SET)

                // 등록 버튼 동일한 위치
                TextButton(onClick = onAddSetClick) {
                    Text(
                        text = "등록하러가기",
                        style = medium13,
                        color = Color(0xFF96BEFF),
                    )
                }
            }

        }

    } else {
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
}