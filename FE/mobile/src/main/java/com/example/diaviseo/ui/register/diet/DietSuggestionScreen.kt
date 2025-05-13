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
import com.example.diaviseo.ui.register.diet.components.RecentFoodList
import com.example.diaviseo.viewmodel.DietSearchViewModel

@Composable
fun DietSuggestionScreen(
    viewModel: DietSearchViewModel = viewModel(),
    navController: NavController
) {
    var selectedCategory by remember { mutableStateOf(0) }
    val categories = listOf("최근", "세트", "음식")

    LaunchedEffect(selectedCategory) {
        when (selectedCategory) {
            0 -> viewModel.fetchRecnetFoods()
            1 -> if (viewModel.foodSets.isEmpty()) {
                viewModel.fetchFoodSets()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SelectableCategoryRow(
            categories = categories,
            selectedIndex = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedCategory) {
            0 -> RecentFoodList(
                foods = viewModel.recentFoods,
                selectedItems = viewModel.selectedItems.map { it.foodId },
                fetchedDate = viewModel.recentFetchedDate,
                onToggleSelect = { viewModel.addRecentFood(it) }
            )
            1 -> FoodSetList(
                foodSets = viewModel.foodSets,
                onAddSetClick = {
                    navController.navigate("food_set_register")
                },
                onSetClick = { selectedSet ->
                    viewModel.clearDietState() // 이전에 음식에서 선택된 목록들 초기화하고 세트 등록 시작
                    viewModel.applyFoodSet(selectedSet) // 세트 음식 덮어쓰기
                    navController.navigate("diet_confirm") // 등록 화면으로 이동
                }
            )
            2 -> DefaultCategoryContent("음식")
        }
    }
}

@Composable
fun DefaultCategoryContent(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "선택된 카테고리: $name")
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "인기 음식 추가 예정")
        }
    }
}
