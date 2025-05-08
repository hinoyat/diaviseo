package com.example.diaviseo.ui.onboarding.pages

import com.example.diaviseo.util.FinalGuideMessageBuilder
import com.example.diaviseo.util.GoalType
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.viewmodel.GoalViewModel
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
import androidx.compose.ui.text.style.TextAlign
import com.example.diaviseo.ui.components.onboarding.PermissionRequestButton

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import com.example.diaviseo.healthconnect.HealthConnectManager


@Composable
fun FinalGuideScreen(navController: NavController, goalViewModel: GoalViewModel, authViewModel: AuthViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    // ✅ Health Connect 관련 객체 초기화
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val healthConnectManager = remember { HealthConnectManager(context) }

    // ✅ 권한 요청 런처 등록
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        // ✅ 사용자가 권한을 모두 허용했는지 확인
        // granted: Set<String>
        if (granted.containsAll(healthConnectManager.getPermissions())) {
            // ✅ 권한 모두 허용됨
            Log.d("HealthConnect", "모든 권한 허용됨")

            // ✅ 모든 권한 허용 시 Health Connect의 실제 데이터 로깅 시도
            coroutineScope.launch {
                healthConnectManager.logAllHealthData()
                healthConnectManager.logRawSteps()

            }
        } else {
            // ⚠️ 일부 권한 거부됨
            Log.w("HealthConnect", "일부 권한 거부됨")
        }
    }

    val name = authViewModel.name.collectAsState().value
    val birthday = authViewModel.birthday.collectAsState().value
    val genderCode = authViewModel.gender.collectAsState().value
    val heightStr = authViewModel.height.collectAsState().value
    val weightStr = authViewModel.weight.collectAsState().value
//    val goalString = goalViewModel.goal.collectAsState().value
    val goalString = authViewModel.goal.collectAsState().value

    val height = heightStr.toFloatOrNull() ?: 160f
    val weight = weightStr.toFloatOrNull() ?: 55f
    val age = 2025 - (birthday.take(4).toIntOrNull() ?: 1999)
    val gender = if (genderCode == "F") "여" else "남"

    val goalType = when (goalString) {
        "감량" -> GoalType.LOSS
        "유지" -> GoalType.MAINTAIN
        "증량" -> GoalType.GAIN
        else -> GoalType.MAINTAIN
    }
    val goalDisplayText = when (goalType) {
        GoalType.LOSS -> "체중 감량"
        GoalType.MAINTAIN -> "체중 유지"
        GoalType.GAIN -> "체중 증가"
    }

    val guideInfo = FinalGuideMessageBuilder.getGuideInfo(
        gender = gender,
        heightCm = height,
        weightKg = weight,
        age = age,
        goalType = goalType
    )

    val actionTipText = when (goalType) {
        GoalType.LOSS -> "감량을 원하신다면, 섭취보다 ${guideInfo.deficit}kcal 이상 더 소모하는 생활 습관이 도움이 될 수 있어요."
        GoalType.MAINTAIN -> "체중을 건강하게 유지하려면 섭취와 소비의 균형을 맞추는 것이 중요해요."
        GoalType.GAIN -> "증량을 원하신다면, 소비보다 ${guideInfo.deficit}kcal 이상 더 섭취하는 것이 도움이 될 수 있어요."
    }

    val guideText = @Composable {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("표준 체중", fontSize = 14.sp)
                Text("${String.format("%.1f", guideInfo.standardWeight)}kg", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("기초 대사량", fontSize = 14.sp)
                Text("${guideInfo.bmr}kcal", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("섭취 권장량 (현재 체중 기준)", fontSize = 14.sp)
                Text("${guideInfo.actualIntake}kcal", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("섭취 권장량 (표준 체중 기준)", fontSize = 14.sp)
                Text("${guideInfo.idealIntake}kcal", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = actionTipText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth(),
            )

        }
    }
    val particle = if (goalDisplayText == "체중 유지") "를" else "을"

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
                    text = "${goalDisplayText}${particle} 목표로 선택해주셨네요.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = actionTipText,
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
//                        title = {
//                            Text(
//                                "건강 가이드",
//                                fontWeight = FontWeight.SemiBold,
//                                modifier = Modifier.fillMaxWidth(),
//                                textAlign = TextAlign.Center
//                            ) },
                        text = {
                            guideText()
                        },
                        confirmButton = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(onClick = { showDialog = false }) {
                                    Text(
                                        "확인",
                                        fontWeight = FontWeight.SemiBold,
                                        color = DiaViseoColors.Main1,
                                    )
                                }
                            }
                        },
                        containerColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "헬스 커넥트를 연동하면 걸음 수, 수면, 심박수 등\n건강 정보를 자동으로 불러올 수 있어요.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Health Connect 연동 버튼 클릭 시 동작
                Button(
                    onClick = {
                        coroutineScope.launch {
                            // ✅ Health Connect 앱 설치 여부 확인
                            val isAvailable = healthConnectManager.isAvailable()
                            if (isAvailable) {
                                // ➕ 설치되어 있으면 권한 요청
                                permissionLauncher.launch(healthConnectManager.getPermissions())

                                // 🔍 걸음 데이터 로그 출력 (테스트용)
                                healthConnectManager.logRawSteps()
                            } else {
                                // 설치 안 됨 → Play Store 링크로 이동
                                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Main1)
                ) {
                    Text("헬스 커넥트 연동하기", color = Color.White)
                }

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "오늘 식단을 검색을 통해 등록해주시거나\n사진을 등록해주시면 AI가 분석해\n오늘 섭취한 칼로리를 계산해서 알려드릴게요.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                PermissionRequestButton(modifier = Modifier.padding(top = 12.dp))

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