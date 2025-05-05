package com.example.diaviseo.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.BottomNavigationBar
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
        containerColor = Color(0xFFDFE9FF)
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
//                      GoalScreen()
                }
                composable("my") {
//                      MyScreen()
                }
            }
        }
    }
    // FAB 메뉴 오버레이는 여전히 Scaffold 바깥에서
    if (isFabMenuOpen.value) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { isFabMenuOpen.value = false }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    repeat(3) { idx ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(100.dp)
                            ) { Box(Modifier.fillMaxSize()) }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = when (idx) {
                                    0 -> "체중/체성분"
                                    1 -> "식단"
                                    else -> "운동"
                                },
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}