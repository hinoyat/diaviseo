package com.example.diaviseo.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.my.*

@Composable
fun MyScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

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
            onEditExercise = { /* TODO */ }
        )

        MySyncSection(
            onConnectClick = { /* TODO */ }
        )




        MySettingsSection(
            onAlarmClick = { /* TODO */ },
            onInquiryClick = { /* TODO */ },
            onFaqClick = { /* TODO */ }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyScreenPreview() {
    val navController = rememberNavController()
    MyScreen(navController = navController)
}
