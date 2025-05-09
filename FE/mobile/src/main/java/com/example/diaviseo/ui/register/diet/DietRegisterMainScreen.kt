package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.viewmodel.DietSearchViewModel
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.register.components.CommonTabBar
import com.example.diaviseo.ui.register.diet.components.CameraFloatingIconButton
import com.example.diaviseo.ui.register.diet.components.SearchSuggestionList

@Composable
fun DietRegisterMainScreen(
    navController: NavController,
) {
    val viewModel: DietSearchViewModel = viewModel()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("오늘 뭐먹지", "즐겨찾기")

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
                onKeywordChange = { viewModel.onKeywordChange(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔄 탭 영역
            CommonTabBar(
                tabTitles = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(6.dp))

            when (selectedTab) {
                0 -> DietSuggestionScreen()
                1 -> FavoriteFoodsContent()
            }

            Spacer(modifier = Modifier.height(80.dp)) // 플로팅 버튼 영역 확보
        }

        // 🔍 검색 결과
        if (viewModel.keyword.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 110.dp)
                    .zIndex(10f),
                contentAlignment = Alignment.TopCenter
            ) {
                SearchSuggestionList(
                    results = viewModel.searchResults,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 80.dp),
                    onItemClick = { /* TODO 음식 클릭시 음식 상세 페이지로 이동 혹은 추가 로직 구현*/ }
                )
            }

        }


        // ➕ 플로팅 버튼
        CameraFloatingIconButton(
            onClick = {
                navController.navigate("diet_ai_register")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp)
                .zIndex(5f)
        )
    }
}

@Composable
fun FavoriteFoodsContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "즐겨찾기 탭 콘텐츠")
    }
}

