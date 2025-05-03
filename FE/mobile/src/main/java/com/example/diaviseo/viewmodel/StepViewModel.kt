package com.example.diaviseo.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.*
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    private var stepSensor: Sensor? = null

//    private val sensorManager by lazy {
//        getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var _stepCount = MutableStateFlow(0)
//    var stepCount: StateFlow<Int> = _stepCount.asStateFlow()
    var stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private var isListenerRegistered = false

    private var initialStepCount: Int? = null

    init {
        checkStepStatus()
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    fun startListening() {
//        stepSensor?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
//        }
        if (stepSensor == null) {
            Log.e("step view", "step couter sensor 없음")
            return
        }
        if (!isListenerRegistered) {   // 중복 등록 방지
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)   // nomal 아님 ui
            isListenerRegistered = true
            Log.d("step view", "step counter 리스너 등록")
        }
    }

    fun stopListening() {
        if (isListenerRegistered) {
            sensorManager.unregisterListener(this)
            isListenerRegistered = false
            Log.d("step view", "step counter 리스너 등록 해제")
        }
    }

    fun refreshStepCount() {
        // 센서 값은 실시간 반영되므로 여기선 별도 로직 불필요
        // UI의 새로고침 버튼과 연결될 수 있도록 함수 제공
    }

    private fun checkStepStatus() {
        viewModelScope.launch {
            Log.d("step view", "뷰 모델 처음 뜨고, $stepSensor")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            // 부팅 후 누적 걸음수, 일일 걸음수 계산 로직 추가 필요
            Log.d("view model", "부팅 후 누적 걸음수 : $totalSteps")

            if (initialStepCount == null) {
                initialStepCount = totalSteps
            }

//            val todaySteps = totalSteps - (initialStepCount ?: 0)
            val todaySteps = totalSteps
            _stepCount.value = todaySteps
            Log.d("view model", "stepCount???? : ${_stepCount.value}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ViewModel이 소멸될 때 리스너 해제 (메모리 누수 방지)
    override fun onCleared() {
        super.onCleared()
        stopListening()
        Log.d("step view", "StepViewModel cleared, listener unregistered.")
    }
}
