package com.example.diaviseo.ui.mypageedit.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.network.user.dto.req.UserUpdateRequest
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.ProfileViewModel

@Composable
fun PhysicalInfoEditScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel
) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    val context = LocalContext.current
    val profile = viewModel.myProfile.collectAsState().value
    LaunchedEffect(profile) {
        height = profile?.height?.toString() ?: ""
        weight = profile?.weight?.toString() ?: ""
    }
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
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "이 정보는 회원 정보 기준입니다.\n건강 기록은 다른 메뉴에서 확인해주세요.",
                fontSize = 14.sp,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = height,
                onValueChange = {
                    if (it.length <= 6 && it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        height = it
                    }
                },
                label = { Text("키 (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    if (it.length <= 6 && it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
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
                    val h = height.toDoubleOrNull()
                    val w = weight.toDoubleOrNull()

                    if (h != null && w != null && h in 100.0..250.0 && w in 30.0..300.0) {
                        viewModel.updateUserProfile(
                            request = UserUpdateRequest(
                                height = h,
                                weight = w
                            ),
                            onSuccess = {
                                viewModel.fetchMyProfile()
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                Toast.makeText(context, "수정 실패: $msg", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "정상적인 숫자를 입력해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
            ) {
                Text("저장", color = Color.White)
            }
        }
    }
}
