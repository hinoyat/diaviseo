package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.my.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf


@Composable
fun MyScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    var isAlarmEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // ← 요거 추가
    ) {
        CommonTopBar(
            onLeftActionClick = { /* TODO */ }
        )

        MyHeaderSection(userName = "김디아")


        MyProfileEditCard(
            navController = navController
        )

        MyPhysicalInfoCard(
            height = 168,
            weight = 50,
            onEditClick = {
                navController.navigate("edit_physical_info")
            }
        )

        MyHealthDataList(
            onEditAllergy = { navController.navigate("edit_allergy") },
            onEditDisease = { navController.navigate("edit_disease") }
        )


        MyExtraDataList(
            onEditExercise = { navController.navigate("edit_exercise") }
        )

        MySyncSection(
            onConnectClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "설정관리",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        MyAlarmSettingCard(
            isEnabled = isAlarmEnabled,
            onToggle = { isAlarmEnabled = it }
        )

        MyFaqCard(onClick = { navController.navigate("faq") })


    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyScreenPreview() {
    val navController = rememberNavController()
    MyScreen(navController = navController)
}
