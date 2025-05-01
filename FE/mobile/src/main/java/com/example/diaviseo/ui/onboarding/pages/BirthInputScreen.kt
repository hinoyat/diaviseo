package com.example.diaviseo.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.onboarding.StepProgressBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.AuthViewModel
import java.time.LocalDate
import java.time.Period

@Composable
fun BirthInputScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // 생년월일 상태 값
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    // 포커스 이동을 위한 객체들
    val focusManager = LocalFocusManager.current
    val monthFocusRequester = remember { FocusRequester() }
    val dayFocusRequester = remember { FocusRequester() }

    // 생년월일 확인 다이얼로그 여부
    var showDialog by remember { mutableStateOf(false) }

    // 입력한 생년월일을 날짜로 파싱
    val birthDate = remember(year, month, day) {
        try {
            if (year.length == 4 && month.length in 1..2 && day.length in 1..2) {
                LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            } else null
        } catch (_: Exception) {
            null
        }
    }

    // 유효성 검사
    val isValid = birthDate != null
    
    // 나이 계산
    val today = LocalDate.now()
    val age = birthDate?.let { Period.between(it, today).years } ?: 0

    // 전체 레이아웃
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
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                // 상단 진행바
                StepProgressBar(currentStep = 3)
                Spacer(modifier = Modifier.height(32.dp))

                // 타이틀 & 서브 텍스트
                Text("생년월일을 입력해주세요", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("만 나이를 기준으로 건강이 분석됩니다!", fontSize = 16.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(200.dp))

                // 생년월일 입력 필드 (연/월/일)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 연도 입력
                    TextField(
                        value = year,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                year = it
                                if (it.length == 4) monthFocusRequester.requestFocus()
                            }
                        },
                        placeholder = { Text("YYYY", fontSize = 20.sp, color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.width(90.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    // 구분자 및 월 입력
                    Text(" / ", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = month,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                month = it
                                if (it.length == 2) dayFocusRequester.requestFocus()
                            }
                        },
                        placeholder = { Text("MM", fontSize = 20.sp, color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .width(80.dp)
                            .focusRequester(monthFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    // 구분자 및 일 입력
                    Text(" / ", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = day,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                day = it
                            }
                        },
                        placeholder = { Text("DD", fontSize = 20.sp, color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .width(80.dp)
                            .focusRequester(dayFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            BottomButtonSection(
                text = "다음",
                enabled = isValid,
                onClick = { showDialog = true }
            )
        }

        // 생년월일 확인 다이얼로그
        if (showDialog && birthDate != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {},
                dismissButton = {},
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("생년월일 확인", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 생년월일 + 나이 텍스트 
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${birthDate.year}년 ")
                                    append("${birthDate.monthValue.toString().padStart(2, '0')}월 ")
                                    append("${birthDate.dayOfMonth.toString().padStart(2, '0')}일, ")
                                    append("만 ${age}세")
                                }
                                append(" 맞나요?")
                            },
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp)) // 버튼과 텍스트 간격 조정

                        // 예/아니요 버튼
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val birth = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
                                    authViewModel.setBirthday(birth)
                                    showDialog = false
                                    navController.navigate("onboarding/body")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DiaViseoColors.Main1,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("예")
                            }

                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD9D9D9),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("아니요")
                            }
                        }
                    }
                }
            )
        }
    }
}
