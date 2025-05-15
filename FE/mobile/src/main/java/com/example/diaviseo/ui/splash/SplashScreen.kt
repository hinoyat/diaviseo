package com.example.diaviseo.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold14
import com.example.diaviseo.ui.theme.semibold10
import com.example.diaviseo.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = viewModel()
) {
    val isLoggedIn by splashViewModel.isLoggedIn.collectAsState()

    // 애니메이션 상태
    var logoAndTextVisible by remember { mutableStateOf(false) } // 로고와 텍스트 함께 제어
    var bottomTextVisible by remember { mutableStateOf(false) }

    // 로고 스케일 애니메이션 (부드럽게 나타남)
    val logoScale by animateFloatAsState(
        targetValue = if (logoAndTextVisible) 1f else 0.8f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "logo_scale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (logoAndTextVisible) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "logo_alpha"
    )

    // 순차적 애니메이션 실행
    LaunchedEffect(Unit) {
        logoAndTextVisible = true // 로고와 텍스트 동시에 나타남
        delay(300)
        bottomTextVisible = true
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            delay(3000)

            if (isLoggedIn == true) {
                navController.navigate("main") {
                    popUpTo(0)
                }
            } else {
                navController.navigate("signup") {
                    popUpTo(0)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 메인 콘텐츠를 위로 이동 (offset 사용)
        Column(
            modifier = Modifier.offset(y = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로고 이미지 - 스케일 + 페이드인 애니메이션
            Image(
                painter = painterResource(id = R.drawable.logo_diaviseo),
                contentDescription = "디아비서 로고",
                modifier = Modifier
                    .size(230.dp)
                    .graphicsLayer(
                        scaleX = logoScale,
                        scaleY = logoScale,
                        alpha = logoAlpha
                    )
            )

            // 고정된 공간 확보
            Spacer(modifier = Modifier.height(32.dp))

            // 텍스트를 위한 고정된 높이 영역
            Column(
                modifier = Modifier.height(64.dp), // 고정 높이로 공간 확보
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = logoAndTextVisible, // 로고와 함께 나타남
                    enter = fadeIn(
                        animationSpec = tween(800, easing = EaseOutCubic)
                    ) + scaleIn(
                        animationSpec = tween(800, easing = EaseOutCubic),
                        initialScale = 0.9f
                    )
                ) {

                }
            }
        }

        // 브랜드 서명 - 위치 조정
        AnimatedVisibility(
            visible = bottomTextVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "POWERED BY",
                    style = semibold10.copy(
                        fontSize = 17.sp,
                        letterSpacing = 0.sp
                    ),
                    color = DiaViseoColors.Deactive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DIA VISION",
                    style = bold14.copy(
                        fontSize = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = DiaViseoColors.Protein
                )
            }
        }
    }
}