package com.example.diaviseo.healthconnect

import android.content.Context
import android.util.Log
import com.example.diaviseo.datastore.HealthConnectDataStore
import com.example.diaviseo.healthconnect.processor.ExerciseSessionRecordProcessor
import com.example.diaviseo.healthconnect.processor.StepDataProcessor
import com.example.diaviseo.viewmodel.register.exercise.ExerciseSyncViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

// 마이페이지 데이터 수동 연동용 로직
object HealthConnectManualSync {

    fun sync(
        context: Context,
        scope: CoroutineScope,
        viewModel: ExerciseSyncViewModel,
        onComplete: () -> Unit = {},
        updateSyncTime: (ZonedDateTime) -> Unit = {}
    ) {
        val manager = HealthConnectManager.createIfAvailable(context)
        if (manager == null) {
            Log.e("ManualSync", "HealthConnectManager is null")
            onComplete()
            return
        }

        scope.launch {
            val sessionRecords = manager.readExerciseSessions()
            val processedExercises = ExerciseSessionRecordProcessor.toRequestList(sessionRecords)

            val stepRecords = manager.readSteps()
            val processedSteps = StepDataProcessor.process(stepRecords)

            var exerciseSynced = false
            var stepSynced = false

            fun checkAndSaveSyncTime() {
                if (exerciseSynced && stepSynced) {
                    val now = ZonedDateTime.now()
                    scope.launch {
                        HealthConnectDataStore.setLastSyncTime(context, now)
                        updateSyncTime(now)
                        Log.d("ManualSync", "✅ 수동 동기화 완료: $now")
                        onComplete()
                    }
                }
            }

            if (processedExercises.isNotEmpty()) {
                viewModel.syncExerciseRecords(
                    requests = processedExercises,
                    onSuccess = {
                        Log.d("ManualSync", "✅ 운동 데이터 전송 완료")
                        exerciseSynced = true
                        checkAndSaveSyncTime()
                    },
                    onError = {
                        Log.e("ManualSync", "❌ 운동 데이터 전송 실패", it)
                    }
                )
            } else {
                exerciseSynced = true
                checkAndSaveSyncTime()
            }

            if (processedSteps.isNotEmpty()) {
                viewModel.syncStepRecords(
                    requests = processedSteps,
                    onSuccess = {
                        Log.d("ManualSync", "✅ 걸음 수 데이터 전송 완료")
                        stepSynced = true
                        checkAndSaveSyncTime()
                    },
                    onError = {
                        Log.e("ManualSync", "❌ 걸음 수 데이터 전송 실패", it)
                    }
                )
            } else {
                stepSynced = true
                checkAndSaveSyncTime()
            }
        }
    }
}