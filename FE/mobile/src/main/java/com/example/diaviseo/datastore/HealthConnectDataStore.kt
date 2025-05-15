package com.example.diaviseo.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object HealthConnectDataStore{
    private val IS_LINKED = booleanPreferencesKey("health_connect_linked")
    private val LAST_SYNC_TIME = stringPreferencesKey("last_health_connect_sync_time")

    private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    // 연동 여부 저장
    suspend fun setLinked(context: Context, isLinked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LINKED] = isLinked

        }
    }

    // 연동 여부 조회
    fun getLinked(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LINKED] ?: false
        }
    }

    // 마지막 동기화 시간 저장
    suspend fun setLastSyncTime(context: Context, time: ZonedDateTime) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_TIME] = time.format(formatter)
        }
    }

    // 마지막 동기화 시간 조회 (null 허용)
    fun getLastSyncTime(context: Context): Flow<ZonedDateTime?> {
        return context.dataStore.data.map { prefs ->
            prefs[LAST_SYNC_TIME]?.let { ZonedDateTime.parse(it,formatter) }
        }
    }
}