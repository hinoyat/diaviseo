package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.register.components.CommonTabBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.diaviseo.ui.register.diet.components.CameraFloatingIconButton
import androidx.compose.ui.zIndex

@Composable
fun DietRegisterMainScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("오늘 뭐먹지", "즐겨찾기")

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            CommonSearchTopBar(
                placeholder = "음식명으로 검색",
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))

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
        }

        CameraFloatingIconButton(
            onClick = {
                navController.navigate("diet_ai_register")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .zIndex(1f)
        )

    }
}
@Composable
fun FavoriteFoodsContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "즐겨찾기 탭 콘텐츠")
    }
}
