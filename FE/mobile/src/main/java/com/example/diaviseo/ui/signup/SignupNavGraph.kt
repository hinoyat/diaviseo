
package com.example.diaviseo.ui.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

fun NavGraphBuilder.signupNavGraph(navController: NavHostController) {
    composable("signup") { SignupScreen(navController) }
    composable("inputName") { InputNameScreen(navController) }
    composable("selectGender") { SelectGenderScreen(navController) }
    composable("inputBirth") { InputBirthScreen(navController) }
}