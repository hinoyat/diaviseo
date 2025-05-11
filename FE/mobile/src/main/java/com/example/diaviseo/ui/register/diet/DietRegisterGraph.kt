package com.example.diaviseo.ui.register.diet

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.diaviseo.viewmodel.DietSearchViewModel

fun NavGraphBuilder.dietGraph(navController: NavHostController) {
    navigation(
        startDestination = "diet_register",
        route = "diet_graph"
    ) {

        // 음식 검색/담기 화면
        composable("diet_register") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("diet_graph")
            }
            val viewModel = viewModel<DietSearchViewModel>(parentEntry)

            DietRegisterMainScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // 담은 음식 확인/기록 화면
        composable("diet_confirm") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("diet_graph")
            }
            val viewModel = viewModel<DietSearchViewModel>(parentEntry)

            DietConfirmScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
