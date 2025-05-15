package com.example.diaviseo.healthconnect.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.diaviseo.datastore.HealthConnectDataStore
import com.example.diaviseo.healthconnect.HealthConnectManager
import com.example.diaviseo.healthconnect.processor.ExerciseSessionRecordProcessor
import com.example.diaviseo.healthconnect.processor.StepDataProcessor
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.flow.firstOrNull
import java.time.ZonedDateTime


class HealthDataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        val isLinked = HealthConnectDataStore.getLinked(context).firstOrNull() ?: false

        if (!isLinked) {
            Log.d("HealthSyncWorker", "⛔ 연동되지 않음 - 동기화 생략")
            return Result.success()
        }

        val manager = HealthConnectManager.createIfAvailable(context) ?:return Result.failure()

        val now = ZonedDateTime.now()
        val lastSync = HealthConnectDataStore.getLastSyncTime(context).firstOrNull() ?: now.minusHours(24)

        val steps = manager.readSteps(lastSync, now)
        val sessions = manager.readExerciseSessions(lastSync, now)

        val stepRequests = StepDataProcessor.process(steps)
        val exerciseRequests = ExerciseSessionRecordProcessor.toRequestList(sessions)

        return try {
            if (exerciseRequests.isNotEmpty()) {
                RetrofitInstance.exerciseApiService.uploadHealthExercises(exerciseRequests)
                Log.d("HealthSyncWorker", "✅ 운동 ${exerciseRequests.size}건 동기화 완료")
            }

            if (stepRequests.isNotEmpty()) {
                RetrofitInstance.exerciseApiService.uploadStepRecords(stepRequests)
                Log.d("HealthSyncWorker", "✅ 걸음 수 ${stepRequests.size}건 동기화 완료")
            }

            // 마지막 동기화 시간 갱신
            HealthConnectDataStore.setLastSyncTime(context, now)
            Log.d("HealthSyncWorker", "✅ 마지막 동기화 시간 저장 완료: $now")

            Result.success()
        } catch (e: Exception) {
            Log.e("HealthSyncWorker", "❌ 동기화 실패", e)
            Result.retry()
        }
    }
}