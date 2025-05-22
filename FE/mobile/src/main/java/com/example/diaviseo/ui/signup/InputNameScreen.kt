package com.example.diaviseo.ui.signup

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.remember
import com.example.diaviseo.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun InputNameScreen(navController: NavController) {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("signupGraph")
    }
    val authViewModel: AuthViewModel = viewModel(parentEntry)

    // 이 authViewModel은 SignupScreen, SelectGenderScreen과 **공유됨**!
}