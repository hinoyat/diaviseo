package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.ui.register.components.SelectableCategoryRow
import com.example.diaviseo.ui.register.diet.components.FoodSetList
import com.example.diaviseo.viewmodel.DietSearchViewModel

@Composable
fun DietSuggestionScreen(
    viewModel: DietSearchViewModel = viewModel(),
    navController: NavController
) {
    var selectedCategory by remember { mutableStateOf(0) }
    val categories = listOf("전체", "인기", "세트", "음식")

    LaunchedEffect(selectedCategory) {
        if (selectedCategory == 2 && viewModel.foodSets.isEmpty()) {
            viewModel.fetchFoodSets()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        SelectableCategoryRow(
            categories = categories,
            selectedIndex = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedCategory) {
            0 -> DefaultCategoryContent("전체")
            1 -> DefaultCategoryContent("인기")
            2 -> FoodSetList(
                foodSets = viewModel.foodSets,
                onAddSetClick = {
                    navController.navigate("food_set_register")
                },
                onSetClick = { selectedSet ->
                    viewModel.applyFoodSet(selectedSet)
                    navController.navigate("diet_confirm")
                }
            )
            3 -> DefaultCategoryContent("음식")
        }
    }
}

@Composable
fun DefaultCategoryContent(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "선택된 카테고리: $name")
    }
}
