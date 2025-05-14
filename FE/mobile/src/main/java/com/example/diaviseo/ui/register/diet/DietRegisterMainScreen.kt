package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.diaviseo.mapper.toFoodItem
import com.example.diaviseo.viewmodel.DietSearchViewModel
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.register.components.CommonTabBar
import com.example.diaviseo.ui.register.diet.components.*
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import kotlinx.coroutines.launch
import com.example.diaviseo.ui.register.diet.components.FavoriteFoodsContent

@Composable
fun DietRegisterMainScreen(
    navController: NavController,
    viewModel: DietSearchViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showMealSheet by remember { mutableStateOf(false) }
    var selectedMeal by remember { mutableStateOf("점심") }

    val tabs = listOf("오늘 뭐먹지", "즐겨찾기")

    var showFoodDetailSheet by remember { mutableStateOf(false) }
    var selectedFoodForDetail by remember { mutableStateOf<FoodDetailResponse?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchFoodSets()
    }
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            viewModel.fetchFavoriteFoods()
            viewModel.clearFavoriteDirtyFlag()
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            // 🔍 검색창
            CommonSearchTopBar(
                placeholder = "음식명으로 검색",
                navController = navController,
                keyword = viewModel.keyword,
                onKeywordChange = { viewModel.onKeywordChange(it) },
                onCancelClick = { viewModel.cancelSearch() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isSearching) {
                SearchSuggestionList(
                    results = viewModel.searchResults,
                    selectedItems = viewModel.selectedItems.map { it.foodId },
                    onFoodClick = { foodItem ->
                        coroutineScope.launch {
                            try {
                                val response = RetrofitInstance.foodApiService.getFoodDetail(foodItem.foodId)
                                val detail = response.data
                                if (detail != null) {
                                    selectedFoodForDetail = detail
                                    showFoodDetailSheet = true
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onToggleSelect = { foodItem ->
                        viewModel.onToggleSelect(foodItem)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                CommonTabBar(
                    tabTitles = tabs,
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(6.dp))

                when (selectedTab) {
                    0 -> DietSuggestionScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                    1 -> FavoriteFoodsContent(
                        viewModel = viewModel,
                        onFoodClick = { foodItem ->
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitInstance.foodApiService.getFoodDetail(foodItem.foodId)
                                    val detail = response.data
                                    if (detail != null) {
                                        selectedFoodForDetail = detail
                                        showFoodDetailSheet = true
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        DietRegisterBottomBar(
            selectedMeal = selectedMeal,
            selectedCount = viewModel.selectedItems.size,
            onMealClick = { showMealSheet = true },
            onRegisterClick = {
                navController.navigate("diet_confirm")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .zIndex(10f)
        )

        if (showFoodDetailSheet && selectedFoodForDetail != null) {
            FoodDetailBottomSheet(
                food = selectedFoodForDetail!!,
                onToggleFavorite = {
                    val foodId = selectedFoodForDetail!!.foodId

                    viewModel.toggleFavorite(foodId) {
                        // 토글 성공 후 상세 정보 다시 불러와서 최신 상태 반영
                        coroutineScope.launch {
                            try {
                                val response = RetrofitInstance.foodApiService.getFoodDetail(foodId)
                                val updated = response.data
                                if (updated != null) {
                                    selectedFoodForDetail = updated
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                ,
                onAddClick = { quantity: Float ->
                    viewModel.addSelectedFood(
                        food = selectedFoodForDetail!!.toFoodItem(),
                        quantity = quantity
                    )
                    showFoodDetailSheet = false
                },
                onDismiss = {
                    showFoodDetailSheet = false

                    // 즐겨찾기 정보가 변경된 적이 있다면
                    if (selectedTab == 1 && viewModel.isFavoriteDirty) {
                        viewModel.fetchFavoriteFoods()
                        viewModel.clearFavoriteDirtyFlag()
                    }
                }
            )

        }

        // ➕ 플로팅 버튼
        CameraFloatingIconButton(
            onClick = {
                navController.navigate("diet_ai_register")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 77.dp)
                .zIndex(15f)
        )

        if (showMealSheet) {
            MealSelectBottomSheet(
                selected = selectedMeal,
                onSelect = { selectedMeal = it },
                onConfirm = { showMealSheet = false },
                onDismiss = { showMealSheet = false }
            )
        }
    }
}
