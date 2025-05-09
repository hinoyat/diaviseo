package com.example.diaviseo.ui.mypageedit.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.CommonTopBar

@Composable
fun PhysicalInfoEditScreen(
    navController: NavHostController,
    initialHeight: Int = 168,
    initialWeight: Int = 50,
    onSave: (Int, Int) -> Unit = { _, _ -> }
) {
    var height by remember { mutableStateOf(TextFieldValue(initialHeight.toString())) }
    var weight by remember { mutableStateOf(TextFieldValue(initialWeight.toString())) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "신체정보 수정",
                onLeftActionClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "이 정보는 회원 정보 기준입니다.\n건강 기록은 다른 메뉴에서 확인해주세요.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 키 입력
            OutlinedTextField(
                value = height,
                onValueChange = {
                    if (it.text.length <= 3 && it.text.all { ch -> ch.isDigit() }) {
                        height = it
                    }
                },
                label = { Text("키 (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 몸무게 입력
            OutlinedTextField(
                value = weight,
                onValueChange = {
                    if (it.text.length <= 3 && it.text.all { ch -> ch.isDigit() }) {
                        weight = it
                    }
                },
                label = { Text("몸무게 (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val heightValue = height.text.toIntOrNull()
                    val weightValue = weight.text.toIntOrNull()

                    if (heightValue != null && weightValue != null &&
                        heightValue in 100..250 && weightValue in 30..300
                    ) {
                        onSave(heightValue, weightValue)
                        navController.popBackStack()
                    } else {
                        // 유효성 검사 실패 시 처리
                        // 예: 스낵바 or Toast
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장")
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhysicalInfoEditScreenPreview() {
    val navController = rememberNavController()
    PhysicalInfoEditScreen(
        navController = navController,
        initialHeight = 170,
        initialWeight = 65
    )
}
