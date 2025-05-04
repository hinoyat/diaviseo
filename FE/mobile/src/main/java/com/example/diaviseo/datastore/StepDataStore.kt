package com.example.diaviseo.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// 1) Context 확장 프로퍼티로 DataStore 인스턴스 생성
val Context.stepDataStore by preferencesDataStore("step_data")

class StepDataStore(private val context: Context) {

    companion object {
        // 자정 기준 부팅 후 누적 걸음 수
        private val KEY_BASE_STEPS = intPreferencesKey("base_steps")
        // 어제 하루 동안 걸음 수
        private val KEY_YESTERDAY_STEPS = intPreferencesKey("yesterday_steps")
    }

    // 2) 자정이 되면 호출: 오늘 자정 기준값(baseSteps)과 어제 걸음수(yesterdaySteps) 저장
    suspend fun saveMidnightSteps(baseSteps: Int, yesterdaySteps: Int) {
        context.stepDataStore.edit { prefs ->
            prefs[KEY_BASE_STEPS] = baseSteps
            prefs[KEY_YESTERDAY_STEPS] = yesterdaySteps
        }
    }

    // 3) 저장된 자정 기준 누적 걸음 수 읽기
    suspend fun getBaseSteps(): Int {
        val prefs = context.stepDataStore.data.first()
        return prefs[KEY_BASE_STEPS] ?: 0
    }

    // 4) 저장된 어제 걸음 수 읽기
    suspend fun getYesterdaySteps(): Int {
        val prefs = context.stepDataStore.data.first()
        return prefs[KEY_YESTERDAY_STEPS] ?: 0
    }
}
