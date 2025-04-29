package com.example.diaviseo.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable

@Composable
fun PhoneAuthScreen(navController: NavController) {
    // === 상태값 선언 영역 ===
    var carrierExpanded by remember { mutableStateOf(false) } // 010 선택 드롭다운 열림 여부
    var carrier by remember { mutableStateOf("010") } // 선택된 휴대폰 앞번호
    var phoneNumber by remember { mutableStateOf("") } // 전화번호 입력값
    var authCode by remember { mutableStateOf("") } // 인증번호 입력값
    val focusManager = LocalFocusManager.current // 포커스 매니저 (터치 시 키보드 내리기)
    var timer by remember { mutableStateOf(180) } // 인증 타이머 (초 단위, 3분)
    var timerStarted by remember { mutableStateOf(false) } // 타이머 시작 여부

    var allChecked by remember { mutableStateOf(false) } // 전체 약관 동의 여부
    var requestClicked by remember { mutableStateOf(false) } // 인증 요청 버튼 클릭 여부

    // 약관 리스트
    val termList = listOf(
        "휴대폰 본인 인증 서비스 이용약관 동의 (필수)",
        "휴대폰 통신사 이용약관 동의 (필수)",
        "개인정보 제공 및 이용 동의 (필수)",
        "고유식별정보 처리 (필수)"
    )
    // 전체 약관 동의 토글 함수
    fun toggleAllChecked() {
        allChecked = !allChecked
    }

    // 타이머 작동
    LaunchedEffect(timerStarted) {
        if (timerStarted) {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
        }
    }

    // === UI 시작 ===
    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지 (그라데이션 배경)
        Image(
            painter = painterResource(id = R.drawable.gradient_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            // 상단 안내 문구
            Text(
                text = "본인확인을 위해\n인증을 진행해 주세요",
                fontSize = 25.sp,
                color = Color.Black,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === 약관 동의 영역 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 6.dp)
            ) {
                // 전체 동의
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) { detectTapGestures { toggleAllChecked() } }
                        .padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (allChecked) R.drawable.checked_square else R.drawable.not_checked_square
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(22.dp) // 아이콘 사이즈 조정
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "본인 인증 서비스 약관 전체 동의",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 개별 약관 항목 리스트
                termList.forEachIndexed { index, term ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = term,
                            fontSize = 14.sp,
                            color = Color(0xFF939292)                                                                                                                                             ,
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.right_gray_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // === 휴대폰 번호 입력 영역 ===
            Text(
                text = "휴대폰번호",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
                )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 휴대폰 앞 번호 선택 박스
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = carrier,
                        onValueChange = {},
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { carrierExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { carrierExpanded = true } // 전체 박스 클릭 가능
                        ,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = DiaViseoColors.Main1.copy(alpha = 0.8f),
                            unfocusedIndicatorColor = Color(0xFF939292)
                        )
                    )
                    // 드롭다운 메뉴
                    DropdownMenu(
                        expanded = carrierExpanded,
                        onDismissRequest = { carrierExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("010", "011", "016").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    carrier = option
                                    carrierExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                // 전화번호 입력창
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("12345678", fontSize = 15.sp, color = Color(0xFFB0B0B0)) },
                    shape = RoundedCornerShape(12.dp),
                    // 휴대폰 번호 입력창
                    trailingIcon = {
                        if (!requestClicked) {
                            // 요청 버튼
                            Text(
                                text = "요청",
                                fontSize = 14.sp,
                                color = DiaViseoColors.Main1,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                if (!timerStarted) {
                                                    timerStarted = true
                                                    timer = 180
                                                    requestClicked = true
                                                }
                                            }
                                        )
                                    }
                            )
                        } else {
                            // 요청 후 '재요청' 표시 (TODO: 이후에 재요청 하는 로직 등 버튼으로 사용)
                            Text(
                                text = "재요청",
                                fontSize = 14.sp,
                                color = Color(0xFF939292), // 회색
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    },

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DiaViseoColors.Main1.copy(alpha = 0.8f),
                        unfocusedIndicatorColor = Color(0xFF939292)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))

            // === 인증번호 입력 영역 ===
                OutlinedTextField(
                    value = authCode,
                    onValueChange = { authCode = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        // 남은 인증 타이머
                        Text(
                            text = String.format("%02d:%02d", timer / 60, timer % 60),
                            color = if (timer == 0) Color.Red else DiaViseoColors.Main1,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DiaViseoColors.Main1.copy(alpha = 0.8f),
                        unfocusedIndicatorColor = Color(0xFF939292)
                    )
                )

            Spacer(
                modifier = Modifier.height(
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 40.dp
                )
            )

            // === 다음 버튼 ===
            Button(
                onClick = { /* 완료 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiaViseoColors.Main1,
                    contentColor = Color.White,
                    disabledContainerColor = DiaViseoColors.Main1,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "다음")
            }
        }
    }
}


