package com.example.diaviseo.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.*
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.diaviseo.datastore.StepDataStore
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
    private val dataStore = StepDataStore(application.applicationContext)

    // UI에서 구독할 StateFlow
    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps

    private val _yesterdaySteps = MutableStateFlow(5870)   // 시연용 원래는 0
    val yesterdaySteps: StateFlow<Int> = _yesterdaySteps

    // 자정 기준 누적값
    private var baseSteps: Int = 0

    private var isListenerRegistered = false

    init {
//        checkStepStatus()
        viewModelScope.launch {
            // baseSteps가 바뀔 때마다 자동 갱신
            dataStore.baseStepsFlow.collect { newBase ->
                if (newBase != baseSteps) {
                    baseSteps = newBase
                    Log.d("StepViewModel", "🕛 baseSteps가 변경됨 → $baseSteps")

                    // 오늘 걸음 수 다시 계산 요청
                    refreshStepCountOnce()
                }
            }
        }

        viewModelScope.launch {
            dataStore.yesterdayStepsFlow.collect {
                _yesterdaySteps.value = it
            }
        }

        // 센서 리스너는 한 번만 시작
        startListening()
        initializeStepCountOnce() // 초기화
    }

    fun startListening() {
//        stepSensor?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
        Log.d("step view", "새로고침이거나 첫 연결이거나")
        if (stepSensor == null) {
            Log.e("step view", "step couter sensor 없음")
            return
        }
        if (!isListenerRegistered) {   // 중복 등록 방지
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)   // nomal 아님 ui
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
        // 현재 누적 센서 값만 한번 가져와서 상태 업데이트 해줌 (모의 동작)
        val lastSensor = stepSensor
        if (lastSensor == null) {
            Log.e("step view", "Sensor not available")
            return
        }

        val event = lastSensor
        Log.d("step view", "강제 새로고침 요청됨, baseSteps: $baseSteps")
    }

//    private fun checkStepStatus() {
//        viewModelScope.launch {
//            Log.d("step view", "뷰 모델 처음 뜨고, $stepSensor")
//            startListening()
//        }
//    }

//    센서 값이 변경될 때마다 호출
    override fun onSensorChanged(event: SensorEvent?) {
    Log.d("StepViewModel", "📊 오늘 걸음 수 업데이트됨 → ${_todaySteps.value}")


    if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val total = event.values[0].toInt() // 부팅 후 누적 걸음 수
            Log.d("step view", "부팅 후 누적 걸음수: $total")

            if (baseSteps > 0) {
                _todaySteps.value = total - baseSteps
            } else {
                // ✅ baseSteps가 아직 없을 경우에도 일단 total 보여줌
                _todaySteps.value = total
                Log.w("StepViewModel", "⚠️ baseSteps 없음, 임시로 total 사용 → todaySteps = 0")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ViewModel이 소멸될 때 리스너 해제 (메모리 누수 방지)
    override fun onCleared() {
        super.onCleared()
        stopListening()
        Log.d("step view", "StepViewModel cleared, listener unregistered.")
    }

    fun initializeStepCountOnce() {
        val context = getApplication<Application>().applicationContext
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val total = event?.values?.get(0)?.toInt() ?: 0
                if (baseSteps != 0) {
                    _todaySteps.value = total - baseSteps
                    Log.d("StepViewModel", "초기 센서 수신 완료: todaySteps = ${_todaySteps.value}")
                }
                sensorManager.unregisterListener(this)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.e("StepViewModel", "TYPE_STEP_COUNTER 센서를 사용할 수 없음.")
        }
    }

    private fun refreshStepCountOnce() {
        // 센서 이벤트를 기다리지 않고 현재 센서 값을 한번 직접 불러오기
        val sensor = stepSensor ?: return
        val sensorManager = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val total = event?.values?.get(0)?.toInt() ?: return
                if (baseSteps > 0) {
                    _todaySteps.value = total - baseSteps
                    Log.d("StepViewModel", "📲 자정 이후 todaySteps 재계산: ${_todaySteps.value}")
                }
                sensorManager.unregisterListener(this)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }


}
