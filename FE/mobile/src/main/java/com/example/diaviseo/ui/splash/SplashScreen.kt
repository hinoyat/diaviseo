package com.example.diaviseo.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.example.diaviseo.viewmodel.SplashViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = viewModel()
) {
    val isLoggedIn by splashViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {  // null 체크: isLoggedIn이 초기화될 때까지 기다린다
            delay(3000) // 3초 대기

            if (isLoggedIn == true) {
                navController.navigate("main") {
                    popUpTo(0) // 스택 비우기
                }
            } else {
                navController.navigate("signup") {
                    popUpTo(0)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✨ CircularProgressIndicator (Material 3 스타일)
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✨ "로딩 중" 텍스트
            Text(
                text = "잠시만 기다려주세요...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}