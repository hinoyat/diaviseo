package com.example.diaviseo.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.stepDataStore by preferencesDataStore("step_data")

class StepDataStore(private val context: Context) {

    companion object {
        private val KEY_BASE_STEPS = intPreferencesKey("base_steps")
        private val KEY_YESTERDAY_STEPS = intPreferencesKey("yesterday_steps")
    }

    val baseStepsFlow: Flow<Int> = context.stepDataStore.data
        .map { it[KEY_BASE_STEPS] ?: 0 }

    val yesterdayStepsFlow: Flow<Int> = context.stepDataStore.data
        .map { it[KEY_YESTERDAY_STEPS] ?: 0 }

    suspend fun saveMidnightSteps(baseSteps: Int, yesterdaySteps: Int) {
        context.stepDataStore.edit { prefs ->
            prefs[KEY_BASE_STEPS] = baseSteps
            prefs[KEY_YESTERDAY_STEPS] = yesterdaySteps
        }
    }

    suspend fun getBaseSteps(): Int {
        return context.stepDataStore.data.first()[KEY_BASE_STEPS] ?: 0
    }

    suspend fun getYesterdaySteps(): Int {
        return context.stepDataStore.data.first()[KEY_YESTERDAY_STEPS] ?: 0
    }
}
