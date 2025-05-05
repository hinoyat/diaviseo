package com.example.diaviseo.ui.main

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.theme.DiaViseoColors
import java.time.LocalDate
import com.example.diaviseo.ui.main.components.goal.*

@Composable
fun GoalScreen(navController: NavHostController) {
    val selectedTab = remember { mutableStateOf("식단") }
    val today = remember { LocalDate.now() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            GoalTopHeader(
                currentDate = today,
                selectedTab = selectedTab.value,
                onTabChange = { selectedTab.value = it }
            )
            GoalContent(
                selectedTab = selectedTab.value,
                today = today
            )
        }
    }
}
