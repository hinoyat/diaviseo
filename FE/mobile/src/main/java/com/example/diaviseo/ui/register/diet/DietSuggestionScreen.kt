package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.mapper.toFoodItem
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.ui.register.components.SelectableCategoryRow
import com.example.diaviseo.ui.register.diet.components.FoodDetailBottomSheet
import com.example.diaviseo.ui.register.diet.components.FoodSetList
import com.example.diaviseo.ui.register.diet.components.RecentFoodList
import com.example.diaviseo.viewmodel.DietSearchViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun DietSuggestionScreen(
    viewModel: DietSearchViewModel = viewModel(),
    navController: NavController
) {
    val profileViewModel: ProfileViewModel = viewModel()
    val profileState by profileViewModel.myProfile.collectAsState()
    val nickname = profileState?.nickname ?: "사용자"

    var selectedCategory by remember { mutableStateOf(0) }
    val categories = listOf("최근", "세트")

    // 서버에서 받아온 상세 음식 정보
    val selectedFood = remember { mutableStateOf<FoodDetailResponse?>(null) }
    val showFoodDetailSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile()
    }
    LaunchedEffect(selectedCategory) {
        when (selectedCategory) {
            0 -> viewModel.fetchRecentFoods()
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
                onToggleSelect = { viewModel.onToggleSelect(it) },
                onFoodClick = { foodItem ->
                    coroutineScope.launch {
                        try {
                            val response = RetrofitInstance.foodApiService.getFoodDetail(foodItem.foodId)
                            val detail = response.data
                            if (detail != null) {
                                selectedFood.value = detail
                                showFoodDetailSheet.value = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                nickname = nickname
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
        }
    }

    // ✅ 바텀시트 표시
    if (showFoodDetailSheet.value && selectedFood.value != null) {
        FoodDetailBottomSheet(
            food = selectedFood.value!!,
            onToggleFavorite = {
                val foodId = selectedFood.value!!.foodId
                viewModel.toggleFavorite(foodId) {
                    coroutineScope.launch {
                        val updated = RetrofitInstance.foodApiService.getFoodDetail(foodId).data
                        if (updated != null) selectedFood.value = updated
                    }
                }
            },
            onAddClick = { quantity ->
                viewModel.addSelectedFood(
                    food = selectedFood.value!!.toFoodItem(),
                    quantity = quantity
                )
                showFoodDetailSheet.value = false
            },
            onDismiss = {
                showFoodDetailSheet.value = false
                // 즐겨찾기 탭일 때 더티 플래그 적용 가능
            }
        )
    }

}
