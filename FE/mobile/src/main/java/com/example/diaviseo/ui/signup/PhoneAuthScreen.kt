package com.example.diaviseo.ui.signup

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

@Composable
fun PhoneAuthScreen(navController: NavController) {
    var carrierExpanded by remember { mutableStateOf(false) }
    var carrier by remember { mutableStateOf("010") }
    var phoneNumber by remember { mutableStateOf("") }
    var authCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var timer by remember { mutableStateOf(180) }
    var timerStarted by remember { mutableStateOf(false) }

    var allChecked by remember { mutableStateOf(false) }
    var showTermsDetail by remember { mutableStateOf(true) }
    var requestClicked by remember { mutableStateOf(false) }

    val termList = listOf(
        "휴대폰 본인 인증 서비스 이용약관 동의 (필수)",
        "휴대폰 통신사 이용약관 동의 (필수)",
        "개인정보 제공 및 이용 동의 (필수)",
        "고유식별정보 처리 (필수)"
    )

    fun toggleAllChecked() {
        allChecked = !allChecked
        showTermsDetail = !allChecked
    }

    LaunchedEffect(timerStarted) {
        if (timerStarted) {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            Text(
                text = "회원 정보를 확인하기 위해\n문자 인증을 진행해 주세요.",
                fontSize = 24.sp,
                color = Color.Black,
                lineHeight = 30.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp)
                    .padding(start = 3.dp, end = 3.dp)
            ) {
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

                    Spacer(modifier = Modifier.height(18.dp))
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            if (!showTermsDetail) {
                Spacer(modifier = Modifier.height(26.dp))
            }

            Text(
                text = "문자인증을 진행해주세요",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("12345678", fontSize = 15.sp, color = Color(0xFFB0B0B0)) },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (!requestClicked) {
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
                            Text(
                                text = "재요청",
                                fontSize = 14.sp,
                                color = Color(0xFF939292),
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
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = authCode,
                onValueChange = { authCode = it },
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 40.dp))

            BottomButtonSection(
                text = "다음",
                onClick = { navController.navigate("onboarding/name") }
            )
        }
    }
}