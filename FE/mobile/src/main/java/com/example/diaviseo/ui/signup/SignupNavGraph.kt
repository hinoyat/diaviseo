
package com.example.diaviseo.ui.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

fun NavGraphBuilder.signupNavGraph(navController: NavHostController) {
    composable("signup") { SignupScreen(navController) }
    composable("inputName") { InputNameScreen(navController) }
    composable("selectGender") { SelectGenderScreen(navController) }
    composable("inputBirth") { InputBirthScreen(navController) }
    composable("phoneAuth") { PhoneAuthScreen(navController) }   // 소셜 로그인 이후 휴대폰 인증
}