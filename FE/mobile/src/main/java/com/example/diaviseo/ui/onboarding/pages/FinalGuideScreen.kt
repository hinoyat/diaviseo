package com.example.diaviseo.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.onboarding.StepProgressBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.GoalViewModel

@Composable
fun FinalGuideScreen(navController: NavController, goalViewModel: GoalViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                StepProgressBar(currentStep = 4)
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "체중 감량을 목표로 선택해주셨네요.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "매일 256kcal 이상 칼로리를 소모하는 경우\n체중을 감량할 수 있어요.",
                    fontSize = 16.sp,
                    color = DiaViseoColors.Main1,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .wrapContentWidth()
                        .defaultMinSize(minHeight = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF0F0F0),
                        contentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "계산 방법이 궁금한가요?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("기초 대사량 계산 방법") },
                        text = {
                            Text("기초 대사량 = 표준 체중 × 30\n(출처: 대한당뇨병학회)")
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("확인")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* TODO: 헬스 커넥트 연동 */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
                ) {
                    Text("헬스 커넥트 연동하기", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "오늘 식단을 검색을 통해 등록해주시거나\n사진을 등록해주시면 AI가 분석해\n오늘 섭취한 칼로리를 계산해서 알려드릴게요.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* TODO: 권한 요청 로직 */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDDDDD))
                ) {
                    Text("카메라, 갤러리 권한 허용하기", color = Color.Black)
                }
            }

            BottomButtonSection(
                text = "디아 비서 시작하기",
                enabled = true,
                onClick = {
                    navController.navigate("main") {
                        popUpTo("signupGraph") { inclusive = true }
                    }
                }
            )
        }
    }
}
