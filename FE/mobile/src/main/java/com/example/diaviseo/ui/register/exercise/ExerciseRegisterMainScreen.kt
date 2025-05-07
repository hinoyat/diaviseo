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

@Composable
fun ExerciseRegisterMainScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("운동 추천", "최근 등록")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
                                                        ) {
        CommonSearchTopBar(
            placeholder = "운동명으로 검색",
            navController = navController
        )

        Spacer(modifier = Modifier.height(12.dp))

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
