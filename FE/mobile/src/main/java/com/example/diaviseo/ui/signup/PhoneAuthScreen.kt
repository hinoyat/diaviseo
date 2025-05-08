package com.example.diaviseo.ui.signup

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlinx.coroutines.delay
import com.example.diaviseo.viewmodel.AuthViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun PhoneAuthScreen(navController: NavController, viewModel: AuthViewModel) {
    // --- 상태 관리 ---
    var carrierExpanded by remember { mutableStateOf(false) } // 앞번호 드롭다운 확장 여부
    var carrier by remember { mutableStateOf("010") } // 선택된 앞번호
    var phoneNumber by remember { mutableStateOf("") } // 전화번호 입력 값
    var authCode by remember { mutableStateOf("") } // 인증번호 입력 값
    val focusManager = LocalFocusManager.current // 포커스 해제용
    var timer by remember { mutableStateOf(180) } // 타이머 (초 단위)
    var timerStarted by remember { mutableStateOf(false) } // 타이머 작동 여부
    var allChecked by remember { mutableStateOf(false) } // 전체 약관 동의 여부
    var showTermsDetail by remember { mutableStateOf(true) } // 하위 약관 보기 여부
    var requestClicked by remember { mutableStateOf(false) } // 요청 버튼 눌렀는지 여부

    val context = LocalContext.current
    val isPhoneAuth by viewModel.isPhoneAuth.collectAsState()

    // --- 약관 리스트 ---
    val termList = listOf(
        "휴대폰 본인 인증 서비스 이용약관 동의 (필수)",
        "휴대폰 통신사 이용약관 동의 (필수)",
        "개인정보 제공 및 이용 동의 (필수)",
        "고유식별정보 처리 (필수)"
    )
    // --- 전체 동의 토글 시 하위 항목 숨기기/펼치기 ---
    fun toggleAllChecked() {
        allChecked = !allChecked
        showTermsDetail = !allChecked
    }

    // --- 타이머 카운트 다운 ---
    LaunchedEffect(timerStarted) {
        if (timerStarted) {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
        }
    }
    LaunchedEffect(timer){
        if (timer == 0) {
            timerStarted = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast
                .makeText(context, message, Toast.LENGTH_LONG)
                .show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 배경 이미지 ---
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
            // --- 상단 안내 문구 ---
            Text(
                text = "회원 정보를 확인하기 위해\n문자 인증을 진행해 주세요.",
                fontSize = 24.sp,
                color = Color.Black,
                lineHeight = 30.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))
            // --- 약관 동의 영역 ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp)
                    .padding(start = 3.dp, end = 3.dp)
            ) {
                // 전체 동의 항목
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
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "본인 인증 서비스 약관 전체 동의",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // 하위 약관 리스트 (펼쳐져 있을 경우만)
                if (showTermsDetail) {
                    Spacer(modifier = Modifier.height(8.dp))
                    termList.forEach { term ->
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
                                color = Color(0xFF939292),
                                modifier = Modifier.weight(1f)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.right_gray_arrow),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(13.dp))
//                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
//                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

//            if (!showTermsDetail) {
//                Spacer(modifier = Modifier.height(0.dp))
//            }

            if (allChecked) {

                // --- 문자인증 섹션 ---
                Text(
                    text = "문자인증을 진행해주세요",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(15.dp))

                // 휴대폰 번호 입력 행 (앞번호 + 전화번호)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 앞번호 드롭다운
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
                                .clickable { carrierExpanded = true },
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
                    // 전화번호 입력
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("12345678", fontSize = 15.sp, color = Color(0xFFB0B0B0)) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            // 유효성 검사: 8자리여야만 true
                            val isPhoneValid = phoneNumber.length == 8
                            val buttonEnabled = !timerStarted && (isPhoneValid && !requestClicked)

                            TextButton(
                                onClick = {
                                    viewModel.setPhone(carrier + phoneNumber)
                                    viewModel.phoneAuthTry{
                                        // onSuccess로 넘어와야 여기 요청 로직 시작
                                        timerStarted = true
                                        timer = 180
                                        requestClicked = true
                                    }
                                },
                                enabled = buttonEnabled,    // 8자리 아니면 비활성
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = if (!requestClicked) "요청" else "재요청",
                                    fontSize = 14.sp,
                                    // 비활성일 땐 회색, 활성일 땐 메인 컬러
                                    color = if (isPhoneValid && !timerStarted) DiaViseoColors.Main1 else Color(0xFFB0B0B0)
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
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 인증번호 입력창 + 타이머
                    OutlinedTextField(
                        value = authCode,
                        onValueChange = { authCode = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),  // 버튼 높이와 맞추고 싶으면 고정
                        singleLine = true,
                        placeholder = { Text("인증 번호 입력", fontSize = 15.sp, color = Color(0xFFB0B0B0)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (requestClicked) {
                                Text(
                                    text = String.format("%02d:%02d", timer / 60, timer % 60),
                                    color = if (timer == 0) Color.Red else DiaViseoColors.Main1,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 12.dp)
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

                    val isConfirmEnabled = authCode.isNotBlank() && requestClicked

                    Button(
                        onClick = {
                            viewModel.setauthCode(authCode)
                            viewModel.phoneAuthConfirm()
                        },
                        enabled = isConfirmEnabled,
                        modifier = Modifier
                            .height(56.dp), // TextField와 동일 높이
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConfirmEnabled) DiaViseoColors.Main1 else Color(0xFFCCCCCC),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = "확인")
                    }
                }
            }

            // 하단 버튼 영역
            Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 40.dp))

            BottomButtonSection(
                text = "다음",
                enabled = isPhoneAuth,
                onClick = { navController.navigate("onboarding/name")
                }
            )
        }
    }
}