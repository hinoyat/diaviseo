package com.example.diaviseo.ui.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
    }
}
