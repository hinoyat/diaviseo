package com.example.diaviseo.ui.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.diaviseo.ui.onboarding.pages.NameInputScreen
import com.example.diaviseo.ui.onboarding.pages.BmiInputScreen

@Composable
fun SignupNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "social") {
        composable("social") {
            SignupScreen(navController)
        }
        composable("inputName") {
            InputNameScreen(navController)
        }
        composable("selectGender") {
            SelectGenderScreen(navController)
        }
        composable("inputBirth") {
            InputBirthScreen(navController)
        }
        composable("phoneAuth") {
            PhoneAuthScreen(navController) // 소셜 로그인 이후 휴대폰 인증
        }
        composable("onboarding/name") {
            NameInputScreen(navController)
        }
        composable ("onboarding/Bmiinput") {
            BmiInputScreen(navController)
        }

}}
