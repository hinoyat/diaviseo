package com.example.diaviseo.utils

import android.util.Log
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object FcmTokenSender {
    fun sendTokenToServer(token: String, onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.alarmApi.sendFcmToken(mapOf("token" to token))
                if (response.status == "200" || response.status == "OK") {
                    Log.d("FCM", "FCM 토큰 전송 성공")
                } else {
                    Log.w("FCM", "FCM 토큰 전송 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "FCM 토큰 전송 중 오류", e)
            } finally {
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }
}

