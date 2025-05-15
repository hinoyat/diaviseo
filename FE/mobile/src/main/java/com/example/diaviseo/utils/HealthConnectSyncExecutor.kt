package com.example.diaviseo.utils

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import com.example.diaviseo.healthconnect.HealthConnectManager
import com.example.diaviseo.healthconnect.HealthConnectPermissionHandler
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.permission.HealthPermission
import com.example.diaviseo.datastore.HealthConnectDataStore
import com.example.diaviseo.healthconnect.processor.ExerciseSessionRecordProcessor
import com.example.diaviseo.healthconnect.processor.StepDataProcessor
import com.example.diaviseo.healthconnect.worker.scheduleDailyHealthSync
import com.example.diaviseo.viewmodel.register.exercise.ExerciseSyncViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

object HealthConnectSyncExecutor {

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    fun requestPermissions(
        launcher: ActivityResultLauncher<Set<String>>
    ) {
        launcher.launch(permissions)
    }

    fun handlePermissionResult(
        context: Context,
        scope: CoroutineScope,
        granted: Set<String>,
        viewModel: ExerciseSyncViewModel
    ) {
        val manager = HealthConnectManager.createIfAvailable(context)
        if (manager == null) {
            Log.e("HealthConnect", "HealthConnectManager is null")
            return
        }

        HealthConnectPermissionHandler.handlePermissionResult(granted, manager, scope)
        scheduleDailyHealthSync(context)

        scope.launch {
            val sessionRecords = manager.readExerciseSessions()
            val processedExercises = ExerciseSessionRecordProcessor.toRequestList(sessionRecords)

            val stepRecords = manager.readSteps()
            val processedSteps = StepDataProcessor.process(stepRecords)

            var exerciseSynced = false
            var stepSynced = false

            fun checkAndSaveSyncTime() {
                if (exerciseSynced && stepSynced) {
                    scope.launch {
                        val now = ZonedDateTime.now()
                        HealthConnectDataStore.setLinked(context, true)
                        HealthConnectDataStore.setLastSyncTime(context, now)
                        Log.d("HealthConnect", "✅ 연동 상태 및 동기화 시간 저장 완료: $now")
                    }
                }
            }

            if (processedExercises.isNotEmpty()) {
                viewModel.syncExerciseRecords(
                    requests = processedExercises,
                    onSuccess = {
                        Log.d("ExerciseSync", "✅ 운동 데이터 서버 전송 성공")
                        exerciseSynced = true
                        checkAndSaveSyncTime()
                    },
                    onError = {
                        Log.e("ExerciseSync", "❌ 운동 데이터 서버 전송 실패", it)
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
                        Log.d("StepSync", "✅ 걸음 수 서버 전송 성공")
                        stepSynced = true
                        checkAndSaveSyncTime()
                    },
                    onError = {
                        Log.e("StepSync", "❌ 걸음 수 서버 전송 실패", it)
                    }
                )
            } else {
                stepSynced = true
                checkAndSaveSyncTime()
            }
        }
    }
}
