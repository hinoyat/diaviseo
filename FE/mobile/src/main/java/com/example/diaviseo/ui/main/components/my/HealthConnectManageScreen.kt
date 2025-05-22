package com.example.diaviseo.ui.main.components.my

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.R
import com.example.diaviseo.utils.HealthConnectSyncExecutor
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.HealthConnectManageViewModel
import com.example.diaviseo.viewmodel.register.exercise.ExerciseSyncViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.example.diaviseo.healthconnect.HealthConnectManualSync
import com.example.diaviseo.healthconnect.HealthConnectPermissionHandler
import com.example.diaviseo.healthconnect.HealthConnectManager


@Composable
fun HealthConnectManageScreen(
    onBackClick: () -> Unit,
    viewModel: HealthConnectManageViewModel = viewModel(),
    syncViewModel: ExerciseSyncViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isConnected by viewModel.isConnected.collectAsState()
    val lastSyncedAt by viewModel.lastSyncedAt.collectAsState()
    val healthConnectManager = remember { HealthConnectManager.createIfAvailable(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        HealthConnectSyncExecutor.handlePermissionResult(
            context = context,
            scope = coroutineScope,
            granted = granted,
            viewModel = syncViewModel
        )
    }
    var isSyncing by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(0f) }

    var showUnlinkDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            rotation.animateTo(
                360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSyncInfo()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CommonTopBar(
            title = "헬스 커넥트 연동",
            onLeftActionClick = onBackClick
        )

        Column(modifier = Modifier.padding(24.dp)) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.health_connect_logo),
                            contentDescription = "헬스 커넥트 로고",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "헬스 커넥트",
                            fontSize = 14.sp,
                            color = DiaViseoColors.Basic
                        )
                    }

                    Text(
                        text = if (isConnected) "해제하기" else "연결하기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isConnected) DiaViseoColors.Unimportant else DiaViseoColors.Main1,
                        modifier = Modifier.clickable {
                            if (isConnected) {
                                showUnlinkDialog = true
                            } else {
                                healthConnectManager?.let {
                                    HealthConnectPermissionHandler.requestPermissionsIfAvailable(
                                        context = context,
                                        scope = coroutineScope,
                                        manager = it,
                                        launcher = permissionLauncher
                                    )
                                } ?: run {
                                    HealthConnectPermissionHandler.redirectToPlayStore(context)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isConnected) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isSyncing) {
                            isSyncing = true
                            HealthConnectManualSync.sync(
                                context = context,
                                scope = coroutineScope,
                                viewModel = syncViewModel,
                                onComplete = { isSyncing = false },
                                updateSyncTime = { now -> viewModel.updateSyncTime(now) }
                            )
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "마지막 동기화: ${lastSyncedAt ?: "정보 없음"}",
                        fontSize = 13.sp,
                        color = DiaViseoColors.Unimportant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "동기화 아이콘",
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { rotationZ = rotation.value },
                        tint = DiaViseoColors.Main1
                    )
                }
            }

            if (showUnlinkDialog) {
                AlertDialog(
                    onDismissRequest = { showUnlinkDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                viewModel.setLinked(false) // 진짜 해제는 여기서
                                viewModel.updateSyncTime(null) // 동기화 시간도 초기화
                                showUnlinkDialog = false
                            }
                        }) {
                            Text("확인", color = DiaViseoColors.Main1, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showUnlinkDialog = false
                        }) {
                            Text("취소", color = Color.Gray)
                        }
                    },
                    title = { Text("정말 해제하시겠습니까?") },
                    text = { Text("헬스 커넥트 연동이 해제되며 건강 정보가 더 이상 불러와지지 않습니다.") },
                    containerColor = Color.White
                )
            }

        }
    }
}

