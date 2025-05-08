package com.example.diaviseo.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    Column(modifier = Modifier.fillMaxSize()) {
        CommonTopBar(
            onLeftActionClick = { /* TODO */ },
            onRightActionClick = { /* TODO */ }
        )

        MyHeaderSection(userName = "김디아")

        MyPhysicalInfoCard(
            height = 168,
            weight = 50,
            onEditClick = {
                navController.navigate("edit_profile")
            }
        )

        MyHealthDataList(
            onEditAllergy = { /* TODO */ },
            onEditDisease = { /* TODO */ }
        )

        MyExtraDataList(
            onEditExercise = { /* TODO */ }
        )

        MySyncSection(
            onConnectClick = { /* TODO */ }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    navController.navigate("edit_profile")
                },
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "회원정보 수정", fontSize = 14.sp)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
        }

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
