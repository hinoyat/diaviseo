package com.example.diaviseo.worker

import android.content.Context
import android.hardware.*
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.diaviseo.datastore.StepDataStore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 매일 자정에 실행되어
 * - 현재 부팅 후 누적 걸음 수(total)
 * - 어제 걸음 수 = total - 이전 자정 기준값(base)
 * 을 DataStore에 저장합니다.
 */
class StepResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dataStore = StepDataStore(applicationContext)

        // 1) 현재 센서 누적 걸음 수 읽기
        val totalSteps = getSensorStepCount(applicationContext)

        // 2) 이전 자정 기준값 읽기
        val baseSteps = dataStore.getBaseSteps()

        // 3) 어제 걸음 수 계산
        val yesterdaySteps = totalSteps - baseSteps

        // 4) DataStore에 새 기준값과 yesterdaySteps 저장
        dataStore.saveMidnightSteps(totalSteps, yesterdaySteps)
        return Result.success()
    }

    // Sensor.TYPE_STEP_COUNTER 센서에서 한 번만 값을 읽어오는 helper 함수
    private suspend fun getSensorStepCount(context: Context): Int =
        suspendCancellableCoroutine { cont ->
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val steps = event?.values?.get(0)?.toInt() ?: 0
                    cont.resume(steps)
                    sensorManager.unregisterListener(this)
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        }
}
