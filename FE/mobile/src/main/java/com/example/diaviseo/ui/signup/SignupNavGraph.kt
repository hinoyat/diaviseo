package com.example.diaviseo.ui.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.example.diaviseo.ui.onboarding.pages.NameInputScreen
import com.example.diaviseo.ui.onboarding.pages.BmiInputScreen

fun NavGraphBuilder.signupNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "signup",
        route = "signupGraph"
    ) {
        composable("signup") { SignupScreen(navController) }
        composable("inputName") { InputNameScreen(navController) }
        composable("selectGender") { SelectGenderScreen(navController) }
        composable("inputBirth") { InputBirthScreen(navController) }
        composable("phoneAuth") { PhoneAuthScreen(navController) }   // 소셜 로그인 이후 휴대폰 인증
        composable("onboarding/name") {
            NameInputScreen(navController)
        }
        composable ("onboarding/Bmiinput") {
            BmiInputScreen(navController)
        }
    }
}