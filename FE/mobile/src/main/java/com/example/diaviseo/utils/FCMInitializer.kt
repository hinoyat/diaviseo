package com.example.diaviseo.utils

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import com.google.firebase.messaging.FirebaseMessaging

object FCMInitializer {

    fun requestNotificationPermissionIfNeeded(
        activity: Activity,
        launcher: ManagedActivityResultLauncher<String, Boolean>,
        onPermissionCheckComplete: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Android 12 이하는 권한 없이 바로 실행
            onPermissionCheckComplete()
        }
    }


    fun fetchAndSendToken(onComplete: () -> Unit = {}) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "✅ 토큰 발급됨: $token")

                // 토큰 전송 후 onComplete() 호출
                FcmTokenSender.sendTokenToServer(token) {
                    onComplete()
                }

            } else {
                Log.e("FCM", "❌ FCM 토큰 발급 실패", task.exception)
                onComplete() // 실패해도 콜백 호출
            }
        }
    }

}
