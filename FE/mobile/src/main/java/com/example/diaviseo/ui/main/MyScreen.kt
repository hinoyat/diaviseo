package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.my.*
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun MyScreen(navController: NavHostController) {
    var isAlarmEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        CommonTopBar(onLeftActionClick = { /* TODO */ })

        Spacer(Modifier.height(16.dp))
        HeaderSection()
        Spacer(Modifier.height(16.dp))

        ProfileSection(navController)
        Spacer(Modifier.height(24.dp))

        HealthSection(navController)
        Spacer(Modifier.height(24.dp))

        SyncSection()
        Spacer(Modifier.height(32.dp))

        SettingSection(
            isAlarmEnabled = isAlarmEnabled,
            onToggle = { isAlarmEnabled = it },
            onFaqClick = { navController.navigate("faq") }
        )
    }
}

@Composable
private fun HeaderSection() {
    MyHeaderSection(userName = "김디아")
}

@Composable
private fun ProfileSection(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyProfileEditCard(navController = navController)
        MyPhysicalInfoCard(
            height = 168,
            weight = 50,
            onEditClick = {
                navController.navigate("edit_physical_info")
            }
        )
    }
}

@Composable
private fun HealthSection(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyHealthDataList(
            onEditAllergy = { navController.navigate("edit_allergy") },
            onEditDisease = { navController.navigate("edit_disease") }
        )
        MyExtraDataList(
            onEditExercise = { navController.navigate("edit_exercise") }
        )
    }
}

@Composable
private fun SyncSection() {
    MySyncSection(onConnectClick = { /* TODO */ })
}

@Composable
private fun SettingSection(
    isAlarmEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onFaqClick: () -> Unit
) {
    Text(
        text = "설정관리",
        fontSize = 16.sp,
        color = DiaViseoColors.Basic,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyAlarmSettingCard(
            isEnabled = isAlarmEnabled,
            onToggle = onToggle
        )

        MyFaqCard(onClick = onFaqClick)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyScreenPreview() {
    val navController = rememberNavController()
    MyScreen(navController = navController)
}
