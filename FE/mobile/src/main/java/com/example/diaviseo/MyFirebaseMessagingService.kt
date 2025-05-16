package com.example.diaviseo

import android.util.Log
import com.example.diaviseo.utils.FcmTokenSender
import com.example.diaviseo.datastore.FcmTokenManager
import com.example.diaviseo.datastore.TokenDataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM Token: $token")

        // 앱 Context 사용
        val context = applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            // 1️⃣ 로컬 저장
            FcmTokenManager.saveToken(context, token)
            Log.d("FCM", "✅ FCM 토큰 저장 완료 (onNewToken)")

            // 2️⃣ accessToken 존재 시 서버 전송
            val accessToken = TokenDataStore.getAccessToken(context).firstOrNull()
            if (!accessToken.isNullOrBlank()) {
                FcmTokenSender.sendTokenToServer(token)
                Log.d("FCM", "✅ 로그인 상태 → FCM 토큰 서버 전송 완료")
            } else {
                Log.d("FCM", "🔒 로그인 안 됨 → FCM 토큰 서버 전송 생략")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "FCM 메시지 수신됨: ${remoteMessage.data}")
    }
}