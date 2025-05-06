package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.BottomNavigationBar
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.diaviseo.ui.main.components.FabOverlayMenu
import com.example.diaviseo.ui.register.bodyregister.BodyDataRegisterScreen

@Composable
fun MainScreen() {
    // 화면 이동을 관리해주는 내비게이션 컨트롤러
    val navController = rememberNavController()
    val userNickname = "김디아" // TODO: ViewModel 등에서 유저 정보 주입
    val isFabMenuOpen = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                isFabMenuOpen = isFabMenuOpen
            )
        },
//        containerColor = Color(0xFFDFE9FF)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    // 기존 HomeScreen을 그대로 재사용
                    HomeScreen(
                        userNickname = userNickname,
                        navController = navController
                    )
                }
                composable("chat") {
                      ChatScreen()
                }
                composable("goal") {
                      GoalScreen(navController)
                }
                composable("my") {
//                      MyScreen()
                }

                composable("body_register") {
                    BodyDataRegisterScreen(navController)
                }
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