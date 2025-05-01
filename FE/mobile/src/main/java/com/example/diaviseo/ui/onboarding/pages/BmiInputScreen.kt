package com.example.diaviseo.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.onboarding.StepProgressBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun BmiInputScreen(
    navController: NavController, viewModel: AuthViewModel
) {
    val name by viewModel.name.collectAsState()

    val gender by viewModel.gender.collectAsState()
    val birthday by viewModel.birthday.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()

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

                StepProgressBar(currentStep = 2)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "디아비서는 운동과 식단을 통해\n" +
                            "건강을 관리 하도록 도와드리고 있어요.",
                    fontSize = 20.sp,
                    color = Color.Black,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

//                Text(
//                    text = "${name.ifBlank { "회원" }}님에게 알맞은 안내를 위해\n" +
//                            "간단한 정보를 알려주세요.",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color.Black
//                )
//                Spacer(modifier = Modifier.height(30.dp))

                // 성별
                Text(
                    text = "아래의 정보를 입력해주시면\n" +
                            "${name.ifBlank { "회원" }}님의 현재의 BMI를 계산해드릴게요.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GenderChip("남성", gender == "M") { viewModel.setGender("M") }
                    GenderChip("여성", gender == "F") { viewModel.setGender("F") }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 생년월일
                OutlinedTextField(
                    value = birthday,
                    onValueChange = { viewModel.setBirthday(it) },
                    placeholder = { Text("예: 2000-01-01") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = DiaViseoColors.Main1,
                        unfocusedIndicatorColor = Color(0xFF939292),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 키 입력
                OutlinedTextField(
                    value = height,
                    onValueChange = { viewModel.setHeight(it) },
                    placeholder = { Text("키 (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = DiaViseoColors.Main1,
                        unfocusedIndicatorColor = Color(0xFF939292),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 몸무게 입력
                OutlinedTextField(
                    value = weight,
                    onValueChange = { viewModel.setWeight(it) },
                    placeholder = { Text("몸무게 (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = DiaViseoColors.Main1,
                        unfocusedIndicatorColor = Color(0xFF939292),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            // 다음 버튼
            BottomButtonSection(
                text = "BMI 계산 결과 보기",
                enabled = gender.isNotBlank() && birthday.isNotBlank() &&
                        height.isNotBlank() && weight.isNotBlank(),
                onClick = {
                    navController.navigate("onboarding/goal") // 다음 화면으로 이동
                }
            )
        }
    }
}

@Composable
fun GenderChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) DiaViseoColors.Main1 else Color(0xFFF0F0F0),
            contentColor = if (selected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(text)
    }
}
