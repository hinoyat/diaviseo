package com.example.diaviseo.ui.register.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.register.components.CommonTabBar
import com.example.diaviseo.viewmodel.ExerciseSearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.ui.register.exercise.components.ExerciseSearchResultList
import com.example.diaviseo.ui.register.components.SelectableCategoryRow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun ExerciseRegisterMainScreen(
    navController: NavController
) {
    val viewModel: ExerciseSearchViewModel = viewModel()
    val keyword by viewModel.keyword.collectAsState()
    val searchResults by viewModel.filteredExercises.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("운동 추천", "최근 등록")
    var isSearchFocused by remember { mutableStateOf(false) }

    // 카테고리 상태
    val categories = listOf("전체", "일반", "유산소", "프리 웨이트", "아웃도어", "수상스포츠", "겨울 스포츠", "구기 종목")
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val selectedCategory = categories[selectedCategoryIndex].takeIf { it != "전체" }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
                                                        ) {
        CommonSearchTopBar(
            placeholder = "운동명으로 검색",
            navController = navController,
            keyword = keyword,
            onKeywordChange = { viewModel.onKeywordChanged(it) },
            onFocusChanged = { isSearchFocused = it },
            onCancelClick = {
                if (isSearchFocused) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                } else {
                    navController.popBackStack()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isSearchFocused) {
            if (keyword.isBlank()) {
                Column {
                    SelectableCategoryRow(
                        categories = categories,
                        selectedIndex = selectedCategoryIndex,
                        onCategorySelected = { selectedCategoryIndex = it }
                    )

//                    Spacer(modifier = Modifier.height(8.dp))

                    ExerciseSearchResultList(
                        exercise = viewModel.getExercisesByCategory(selectedCategory)
                    )
                }
            }
            else {
                // ✅ 검색어가 입력된 경우: 필터링된 결과 표시
                ExerciseSearchResultList(
                    exercise = searchResults
                )
            }
        } else {
            // 기본 UI: 탭 + 콘텐츠
            CommonTabBar(
                tabTitles = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> ExerciseRecommendationContent()
                1 -> RecentExerciseContent()
            }
        }
    }
}

@Composable
fun ExerciseRecommendationContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "운동 추천 탭 콘텐츠")
    }
}

@Composable
fun RecentExerciseContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "최근 등록 탭 콘텐츠")
    }
}
