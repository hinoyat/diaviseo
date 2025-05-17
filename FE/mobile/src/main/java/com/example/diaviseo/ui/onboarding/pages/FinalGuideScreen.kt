package com.example.diaviseo.ui.onboarding.pages

import android.app.Activity
import com.example.diaviseo.util.FinalGuideMessageBuilder
import com.example.diaviseo.util.GoalType
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.healthconnect.HealthConnectManager
import com.example.diaviseo.healthconnect.HealthConnectPermissionHandler
import com.example.diaviseo.healthconnect.processor.StepDataProcessor
import com.example.diaviseo.healthconnect.processor.ExerciseSessionRecordProcessor
import com.example.diaviseo.viewmodel.register.exercise.ExerciseSyncViewModel
import com.example.diaviseo.datastore.HealthConnectDataStore
import com.example.diaviseo.healthconnect.worker.scheduleDailyHealthSync
import com.example.diaviseo.utils.FCMInitializer
import java.time.ZonedDateTime

@Composable
fun FinalGuideScreen(
    navController: NavController,
    goalViewModel: GoalViewModel,
    authViewModel: AuthViewModel
) {
    val syncViewModel: ExerciseSyncViewModel = viewModel()

    val now = ZonedDateTime.now()

    var showDialog by remember { mutableStateOf(false) }

    val name = authViewModel.name.collectAsState().value
    val birthday = authViewModel.birthday.collectAsState().value
    val genderCode = authViewModel.gender.collectAsState().value
    val heightStr = authViewModel.height.collectAsState().value
    val weightStr = authViewModel.weight.collectAsState().value
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
    val context = LocalContext.current

    // 현재 Activity 참조
    val activity = context as Activity

    val moveToMain = remember { mutableStateOf(false) }

    LaunchedEffect(moveToMain.value) {
        if (moveToMain.value) {
            navController.navigate("main") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    // 알림 권한 요청 런처 (Android 13 이상에서만 의미 있음)
    val fcmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            Log.d("NAVIGATION", "Permission result: $it")
        }
    )


    val particle = if (goalDisplayText == "체중 유지") "를" else "을"
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        val manager = HealthConnectManager.createIfAvailable(context)

        // 사용자가 권한을 허용했는지에 따라 내부 로직 실행
        if (manager != null) {
            HealthConnectPermissionHandler.handlePermissionResult(
                granted = granted,
                manager = manager,
                scope = coroutineScope
            )
            // 워커 스케줄 등록
            scheduleDailyHealthSync(context)

            coroutineScope.launch {
                // 1. Health Connect에서 운동 기록 조회 + 가공
                val sessionRecords = manager.readExerciseSessions()
                val processedExercises = ExerciseSessionRecordProcessor.toRequestList(sessionRecords)

                Log.d("ExerciseProcessed", "✅ 운동 ${processedExercises.size}건")

                sessionRecords.forEach { record ->
                    val uid = record.metadata.id
                    val startTime = record.startTime
                    val type = record.exerciseType

                    Log.d("HealthConnectUID", "🧾 UID: $uid, 시작 시간: $startTime, 운동 종류: $type")
                }
                processedExercises.forEach {
                    Log.d("ExerciseProcessed", it.toString())
                }

                //  2. Health Connect에서 걸음 수 조회 + 가공
                val stepRecords = manager.readSteps()
                val processedSteps = StepDataProcessor.process(stepRecords)
                Log.d("StepProcessed", "✅ 걸음 수 ${processedSteps.size}건")

                processedSteps.forEach {
                    Log.d("StepProcessed", it.toString())
                }

                var exerciseSynced = false
                var stepSynced = false

                fun checkAndSaveSyncTime() {
                    if (exerciseSynced && stepSynced) {
                        coroutineScope.launch {
                            val now = ZonedDateTime.now()
                            HealthConnectDataStore.setLinked(context, true)
                            HealthConnectDataStore.setLastSyncTime(context, now)
                            Log.d("HealthConnect", "✅ 연동 상태 및 마지막 동기화 시간 저장 완료: $now")
                        }
                    }
                }

                // 3. 운동 데이터 서버 전송
                if (processedExercises.isNotEmpty()) {
                    syncViewModel.syncExerciseRecords(
                        requests = processedExercises,
                        onSuccess = {
                            Log.d("ExerciseSync", "✅ 운동 데이터 서버 전송 성공")
                            exerciseSynced = true
                            checkAndSaveSyncTime()
                        },
                        onError = {
                            Log.e("ExerciseSync", "❌ 운동 데이터 서버 전송 실패", it)
                        }
                    )
                } else {
                    // 운동 데이터가 아예 없는 경우
                    exerciseSynced = true
                    checkAndSaveSyncTime()
                }

                // 4. 걸음수 데이터 서버 전송
                if (processedSteps.isNotEmpty()) {
                    syncViewModel.syncStepRecords(
                        requests = processedSteps,
                        onSuccess = {
                            Log.d("StepSync", "✅ 걸음 수 서버 전송 성공")
                            stepSynced = true
                            checkAndSaveSyncTime()
                        },
                        onError = {
                            Log.e("StepSync", "❌ 걸음 수 서버 전송 실패", it)
                        }
                    )
                } else {
                    // 걸음 수 데이터가 아예 없는 경우
                    stepSynced = true
                    checkAndSaveSyncTime()
                }
            }
        } else {
            Log.e("HealthConnect", "권한 결과 처리 중 HCManager null")
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    modifier = Modifier.wrapContentWidth().defaultMinSize(minHeight = 32.dp),
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

                Button(
                    onClick = {
                        val manager = HealthConnectManager.createIfAvailable(context)
                        if (manager != null) {
                            HealthConnectPermissionHandler.requestPermissionsIfAvailable(
                                context = context,
                                scope = coroutineScope,
                                manager = manager,
                                launcher = permissionLauncher
                            )
                        } else {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Log.e("HealthConnect", "Play Store 실행 실패")
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
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        fcmPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        moveToMain.value = true
                    } else {
                        moveToMain.value = true
                    }
                }
            )
        }
    }
}