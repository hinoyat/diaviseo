package com.example.diaviseo.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 생성
val Context.dataStore by preferencesDataStore(name = "user_prefs")

object TokenDataStore {
    // key 생성
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    // 토큰 저장
    suspend fun saveAccessToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
        }
    }

    suspend fun saveRefreshToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[REFRESH_TOKEN] = token
        }
    }

    // 토큰 가져오기
    fun getAccessToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN]
        }
    }

    fun getRefreshToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN]
        }
    }

    // 토큰 삭제 (로그아웃용)
    suspend fun clearAccessToken(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
        }
        context.dataStore.edit { prefs ->
            prefs.remove(REFRESH_TOKEN)
        }
    }
}
