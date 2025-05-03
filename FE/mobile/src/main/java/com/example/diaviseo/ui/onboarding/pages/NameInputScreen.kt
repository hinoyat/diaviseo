package com.example.diaviseo.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.text.font.FontWeight
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.ui.components.BottomButtonSection
 import com.example.diaviseo.ui.components.onboarding.StepProgressBar

@Composable
fun NameInputScreen(
    navController: NavController, viewModel: AuthViewModel
) {
    var name by remember { mutableStateOf(viewModel.name.value) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.gradient_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues()
                )
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                // 상단 진행바 (1단계 표시) — 사용하지 않도록 주석 처리함
                 StepProgressBar(currentStep = 1)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "디아비서에 오신 걸 환영해요 ✨",
                    fontSize = 24.sp,
                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,

                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "어떤 이름으로 불러드리면 좋을까요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))

                // 입력 필드
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 8) name = it // 8자 제한 적용
                    },
                    placeholder = { Text("이름(닉네임)을 적어주세요.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = DiaViseoColors.Main1,
                        unfocusedIndicatorColor = Color(0xFF939292),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                // 글자 수 제한 안내
                Text(
                    text = "최대 8자까지 입력할 수 있어요.",
                    fontSize = 12.sp,
                    color = Color(0xFF939292),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 하단 다음 버튼
            BottomButtonSection(
                text="다음",
                enabled = name.isNotBlank(),
                onClick = {
                    viewModel.setName(name)
                    navController.navigate("onboarding/Bmiinput")
                }
            )
        }
    }
}
