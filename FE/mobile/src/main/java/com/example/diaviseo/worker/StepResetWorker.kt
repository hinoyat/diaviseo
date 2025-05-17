package com.example.diaviseo.worker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.diaviseo.datastore.StepDataStore
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import kotlin.coroutines.resume

class StepResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val dataStore = StepDataStore(applicationContext)

    override suspend fun doWork(): Result = runCatching {
        // 1. 누적 걸음 수 읽기
        val totalSteps = getStepCounterValue(applicationContext)

        // 2. 이전 자정 기준값 읽기
        val baseSteps = dataStore.getBaseSteps()

        // 3. 어제 걸음 수 계산
        val yesterdaySteps = totalSteps - baseSteps

        // 4. DataStore에 새 기준값과 어제값 저장
        dataStore.saveMidnightSteps(totalSteps, yesterdaySteps)
        Log.d("StepWorker", "✅ 자정 기준값 저장 완료: base=$totalSteps, yesterday=$yesterdaySteps")

        // 5. 오늘 걸음 수 백엔드 전송
//        sendStepsToBackend(yesterdaySteps)

        Result.success()
    }.getOrElse { e ->
        Log.e("StepWorker", "❌ 작업 실패", e)
        Result.failure()
    }

    /**
     * 센서에서 현재 누적 걸음 수를 비동기로 읽어옴
     */
    private suspend fun getStepCounterValue(context: Context): Int =
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
            sensor?.let {
                sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
            } ?: run {
                cont.resume(0)
                Log.e("StepWorker", "❗ 센서 없음, 기본값 0 반환")
            }
        }

    /**
     * 백엔드 API 호출하여 걸음 수 전송
     */
//    private suspend fun sendStepsToBackend(stepCount: Int) {
//        try {
//            val response = ApiClient.stepApi.sendDailySteps(stepCount)
//            if (response.isSuccessful) {
//                Log.d("StepWorker", "✅ 걸음 수 백엔드 전송 성공: $stepCount")
//            } else {
//                Log.e("StepWorker", "❌ 걸음 수 전송 실패: ${response.code()}")
//            }
//        } catch (e: HttpException) {
//            Log.e("StepWorker", "❌ 네트워크 오류", e)
//        } catch (e: Exception) {
//            Log.e("StepWorker", "❌ 알 수 없는 오류", e)
//        }
//    }
}
