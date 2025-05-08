package com.example.diaviseo.ui.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.example.diaviseo.ui.onboarding.pages.NameInputScreen
import com.example.diaviseo.ui.onboarding.pages.BmiInputScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.ui.onboarding.pages.GoalSelectScreen
import com.example.diaviseo.ui.onboarding.pages.FinalGuideScreen
import com.example.diaviseo.viewmodel.GoalViewModel

fun NavGraphBuilder.signupNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "signup",
        route = "signupGraph"
    ) {
//        composable("signup") { SignupScreen(navController) }
        composable("signup") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel: AuthViewModel = viewModel(parentEntry)
            SignupScreen(navController, authViewModel)
        }
        // 휴대폰 인증
        composable("phoneAuth") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel: AuthViewModel = viewModel(parentEntry)
            PhoneAuthScreen(navController, authViewModel)
        }
        
        // 온보딩 1 - 이름
        composable("onboarding/name") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel = viewModel<AuthViewModel>(parentEntry)
            NameInputScreen(navController, authViewModel)
        }

        // 온보딩 2 - 키, 체중 정보 -> BMI
        composable ("onboarding/Bmiinput") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel = viewModel<AuthViewModel>(parentEntry)
            BmiInputScreen(navController, authViewModel)
        }

        // 온보딩 3 - 목표 설정
        composable ("onboarding/goal") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel = viewModel<AuthViewModel>(parentEntry)
            val goalViewModel = viewModel<GoalViewModel>(parentEntry)
            GoalSelectScreen(navController, authViewModel, goalViewModel)
        }

        // 온보딩 4 - 연동 & 권한 허용 등 마지막 단계
        composable("onboarding/finalguide") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signupGraph")
            }
            val authViewModel = viewModel<AuthViewModel>(parentEntry)
            val goalViewModel = viewModel<GoalViewModel>(parentEntry)

            FinalGuideScreen(navController , goalViewModel, authViewModel)
        }

    }
}