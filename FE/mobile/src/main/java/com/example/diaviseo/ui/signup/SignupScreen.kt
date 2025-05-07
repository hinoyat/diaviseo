package com.example.diaviseo.ui.signup

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager // 변경된 import
import androidx.compose.foundation.pager.rememberPagerState // 변경된 import
import androidx.compose.foundation.shape.CircleShape // Indicator에 사용
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Indicator에 사용
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Activity Context 얻기 위해 필요할 수 있음
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.signup.components.SocialLoginButtons
import com.example.diaviseo.ui.signup.socialSignup.GoogleLoginManager
import com.example.diaviseo.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

// @OptIn(ExperimentalPagerApi::class) // 더 이상 필요 없음 (필요시 ExperimentalFoundationApi 사용)
// import androidx.compose.foundation.ExperimentalFoundationApi // 필요시 추가

@Composable
fun SignupScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current // Activity Context 필요 시
    val isLoading = authViewModel.isLoading

    val images = remember { // 불필요한 recomposition 방지
        listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
        )
    }

    // 1. rememberPagerState 수정: pageCount를 람다로 전달
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size } // 페이지 수를 람다로 제공
    )
    val coroutineScope = rememberCoroutineScope()

    // 자동 슬라이드
    LaunchedEffect(pagerState.pageCount) { // key를 pageCount로 변경하여 페이지 수 변경 시 재시작
        while (true) {
            delay(2000)
            // 페이지 수가 0이 아닌 경우에만 실행
            if (pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 120.dp), // 패딩 유지
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. HorizontalPager 수정: count 파라미터 제거
        HorizontalPager(
            state = pagerState, // count는 state에 포함됨
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f) // 비중 유지
        ) { page -> // page 인덱스는 람다의 파라미터로 제공됨
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp), // 하단 패딩 유지
                contentScale = ContentScale.Crop // 이미지 스케일 유지
            )
        }

        // 3. HorizontalPagerIndicator 대체: 커스텀 Indicator 구현
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp), // 상하 패딩 조정
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.Black else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp) // 인디케이터 간 간격
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp) // 인디케이터 크기
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp)) // 간격 유지

        // 소셜 로그인 버튼 (기존 로직 유지)
        SocialLoginButtons(
            onGoogleLoginClicked = {
                // Activity Context를 얻는 더 안전한 방법 사용
                val activity = context as? Activity
                if (activity != null) {
//                    performLogin이 진짜
                    GoogleLoginManager.performLogin(
//                    GoogleLoginManager.performTest(
                        activity = activity,
                        onSuccess = { email, name, idToken, activity ->
                            authViewModel.setEmail(email ?: "") // Null 처리 추가
                            authViewModel.setName(name ?: "") // Null 처리 추가
                            authViewModel.setProvider("google")

                            authViewModel.loginWithGoogle(idToken, activity) { success, isNewUser ->
                                if (success) {
                                    if (isNewUser) {
                                        navController.navigate("phoneAuth")
                                    } else {
                                        navController.navigate("main")
                                    }
                                } else {
                                    Log.d("signupscreen", "로그인 실패")
                                }
                            }
                        },
                        onError = { e ->
                            e.printStackTrace() // 에러 로깅
                        }
                    )
                } else {
                    // Activity Context를 얻을 수 없는 경우 처리 (예: 로그 출력)
                    println("Error: Could not get Activity context for Google Login")
                }
            },
            onKakaoLoginClicked = {
                // TODO: 카카오 로그인 연결 예정
            },
            onNaverLoginClicked = {
                // TODO: 네이버 로그인 연결 예정
            }
        )

    }

    // 회원확인 중일 때 스피너, 디자인 변경 필요
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))  // 반투명 검정 레이어
                .zIndex(1f), // 다른 UI 위에 떠야 하니까 zIndex 추가
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = Color.White // 흰색 스피너
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "회원확인 중...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
