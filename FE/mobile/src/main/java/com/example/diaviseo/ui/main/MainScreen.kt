package com.example.diaviseo.ui.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.diaviseo.ui.components.BottomNavigationBar
import com.example.diaviseo.ui.main.components.FabOverlayMenu
import com.example.diaviseo.ui.mypageedit.screen.PhysicalInfoEditScreen
import com.example.diaviseo.ui.mypageedit.screen.UserProfileEditScreen
import com.example.diaviseo.ui.register.bodyregister.BodyDataRegisterScreen
import com.example.diaviseo.ui.register.diet.DietRegisterMainScreen
import com.example.diaviseo.ui.register.exercise.ExerciseRegisterMainScreen
import com.example.diaviseo.ui.register.diet.DietAiRegisterScreen
import com.example.diaviseo.viewmodel.ProfileViewModel

@Composable
fun MainScreen() {
    // 화면 뜨자마자 회원정보 불러오기
    val profileViewModel = ProfileViewModel()
    // 화면 진입 시 한 번 실행됨
    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile()
    }
    val profile = profileViewModel.myProfile
    Log.d("Mainscreen profileviewModel", "$profile")

    // 화면 이동을 관리해주는 내비게이션 컨트롤러
    val navController = rememberNavController()
    val userNickname = "김디아"
    val isFabMenuOpen = remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideBottomBarRoutes = listOf("body_register", "diet_register", "exercise_register","diet_ai_register")
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
            // 마이페이지
            composable("my") {
                Box(modifier = Modifier.padding(innerPadding)) {
                    MyScreen(navController)
                }
            }
            // 회원 정보 수정
            composable("edit_profile") {
                UserProfileEditScreen(navController)
            }

            composable("edit_physical_info") {
                PhysicalInfoEditScreen(
                    navController = navController,
                    initialHeight = 168,
                    initialWeight = 50,
                    onSave = { height, weight ->
                        // TODO: ViewModel에 전달하거나 저장 처리
                    }
                )
            }


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
