package com.example.diaviseo.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.diaviseo.datastore.HealthConnectDataStore
import androidx.lifecycle.AndroidViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import android.app.Application

class HealthConnectManageViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected


    private val _lastSyncedAt = MutableStateFlow<String?>(null)
    val lastSyncedAt: StateFlow<String?> = _lastSyncedAt

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun loadSyncInfo() {
        viewModelScope.launch {
            HealthConnectDataStore.getLinked(context).collect { linked ->
                _isConnected.value = linked
            }
        }
    }
    fun updateSyncTime(now: ZonedDateTime?) {
        _lastSyncedAt.value = now?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }

    fun syncNow() {
        viewModelScope.launch {
            val now = ZonedDateTime.now()
            HealthConnectDataStore.setLastSyncTime(context, now)
            _lastSyncedAt.value = now.format(timeFormatter)
        }
    }

    fun setLinked(isLinked: Boolean) {
        viewModelScope.launch {
            HealthConnectDataStore.setLinked(context, isLinked)
            _isConnected.value = isLinked
        }
    }
}