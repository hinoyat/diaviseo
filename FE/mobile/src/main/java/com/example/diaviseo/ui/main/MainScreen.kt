package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.diaviseo.ui.components.BottomNavigationBar
import com.example.diaviseo.ui.detail.ExerciseDetailScreen
import com.example.diaviseo.ui.main.components.FabOverlayMenu
import com.example.diaviseo.ui.register.bodyregister.BodyDataRegisterScreen
import com.example.diaviseo.ui.register.diet.DietRegisterMainScreen
import com.example.diaviseo.ui.register.exercise.ExerciseRegisterMainScreen
import com.example.diaviseo.ui.register.diet.DietAiRegisterScreen
import java.time.LocalDate

@Composable
fun MainScreen() {
    // 화면 이동을 관리해주는 내비게이션 컨트롤러
    val navController = rememberNavController()
    val userNickname = "김디아" // TODO: ViewModel 등에서 유저 정보 주입
    val isFabMenuOpen = remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideBottomBarRoutes = listOf("body_register", "diet_register", "exercise_register", "diet_ai_register", "exercise_detail/{date}")
    val isBottomBarVisible = currentRoute !in hideBottomBarRoutes

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                BottomNavigationBar(
                    navController = navController,
                    isFabMenuOpen = isFabMenuOpen
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize() // ✅ 여기서는 padding 제거
        ) {
            composable("home") {
                // 기존 HomeScreen을 그대로 재사용
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(userNickname = userNickname, navController = navController)
                }
            }
            composable("chat") {
                Box(modifier = Modifier.padding(innerPadding)) {
                    ChatScreen()
                }
            }
            composable("goal") {
                Box(modifier = Modifier.padding(innerPadding)) {
                    GoalScreen(navController)
                }
            }
            composable("my") {
                // MyScreen() 등 필요 시 추가
            }

            // ✅ 등록화면은 padding 없음
            composable("body_register") {
                BodyDataRegisterScreen(navController)
            }
            composable("diet_register") {
                DietRegisterMainScreen(navController)
            }
            composable("exercise_register") {
                ExerciseRegisterMainScreen(navController)
            }
            composable("diet_ai_register") {
                DietAiRegisterScreen(navController)
            }

            // 운동 상세화면
            composable("exercise_detail") { backStackEntry ->
                ExerciseDetailScreen(
                    navController = navController
                )
            }
        }
    }
    // 조건부 UI는 Scaffold 바깥에!
    // 하단바 + 버튼 토글 (컴포넌트로 분리)
    if (isFabMenuOpen.value) {
        FabOverlayMenu(
            onDismiss = { isFabMenuOpen.value = false },
            navController = navController
        )
    }
}
