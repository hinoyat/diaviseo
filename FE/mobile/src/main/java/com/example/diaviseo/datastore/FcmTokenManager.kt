package com.example.diaviseo.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Context 확장 프로퍼티로 DataStore 정의
private val Context.fcmTokenDataStore by preferencesDataStore(name = "fcm_token_prefs")

object FcmTokenManager {
    // 🔑 문자열 타입 키 선언
    private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")

    // 저장
    suspend fun saveToken(context: Context, token: String) {
        context.fcmTokenDataStore.edit { prefs ->
            prefs[FCM_TOKEN_KEY] = token
        }
    }

    // 조회
    suspend fun getToken(context: Context): String? {
        return context.fcmTokenDataStore.data
            .map { prefs -> prefs[FCM_TOKEN_KEY] }
            .first()
    }

    // 삭제 (예: 로그아웃 시)
    suspend fun clearToken(context: Context) {
        context.fcmTokenDataStore.edit { prefs ->
            prefs.remove(FCM_TOKEN_KEY)
        }
    }

}