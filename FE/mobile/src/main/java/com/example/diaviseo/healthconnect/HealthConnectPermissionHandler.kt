package com.example.diaviseo.healthconnect

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.content.Intent

object HealthConnectPermissionHandler{

    // 사용자 권한 요청 결과 처리
    fun handlePermissionResult(
        granted: Set<String>,
        manager: HealthConnectManager,
        scope: CoroutineScope
    ){
        if (granted.containsAll(manager.getPermissions())) {
            Log.d("HealthConnect", "✅ 모든 권한 허용됨")
            scope.launch {
//                manager.logAllHealthData()
//                manager.logRawSteps()
            }
        } else {
                Log.w("HealthConnect", "⚠️ 일부 권한이 거부됨")
            }
        }

    // 설치 여부 확인 -> 설치돼 있으면 권한 요청, 아니면 playstore 이동
    fun requestPermissionsIfAvailable(
        context: Context,
        scope: CoroutineScope,
        manager: HealthConnectManager,
        launcher: ActivityResultLauncher<Set<String>>
    ){
        scope.launch {
            val isInstalled = HealthConnectManager.isAvailable(context)
            if (isInstalled) {
                launcher.launch(manager.getPermissions())
            } else {
                redirectToPlayStore(context)
            }
        }
    }

    private fun redirectToPlayStore(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 인텐트 가능한 앱 있는지 먼저 확인
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.e("HealthConnect", "Play Store를 실행할 수 없습니다.")
        }
    }

}