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

    private val _yesterdaySteps = MutableStateFlow(0)
    val yesterdaySteps: StateFlow<Int> = _yesterdaySteps

    // 자정 기준 누적값
    private var baseSteps: Int = 0

    private var isListenerRegistered = false

    init {
        checkStepStatus()
        // 1) DataStore에서 지난 자정 데이터 불러오기
        viewModelScope.launch {
            baseSteps = dataStore.getBaseSteps()
            _yesterdaySteps.value = dataStore.getYesterdaySteps()
        }
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
        // 센서 값은 실시간 반영되므로 여기선 별도 로직 불필요
    }

    private fun checkStepStatus() {
        viewModelScope.launch {
            Log.d("step view", "뷰 모델 처음 뜨고, $stepSensor")
            startListening()
        }
    }

//    센서 값이 변경될 때마다 호출
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val total = event.values[0].toInt()       // 부팅 이후 누적 걸음 수
            Log.d("view model", "부팅 후 누적 걸음수 : $total")
            _todaySteps.value = total - baseSteps     // 오늘 걸음 수 계산
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
