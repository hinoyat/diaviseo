package com.example.diaviseo.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private var initialStepCount: Int? = null

    init {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    fun startListening() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun refreshStepCount() {
        // 센서 값은 실시간 반영되므로 여기선 별도 로직 불필요
        // UI의 새로고침 버튼과 연결될 수 있도록 함수 제공
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()

            if (initialStepCount == null) {
                initialStepCount = totalSteps
            }

            val todaySteps = totalSteps - (initialStepCount ?: 0)
            _stepCount.value = todaySteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
