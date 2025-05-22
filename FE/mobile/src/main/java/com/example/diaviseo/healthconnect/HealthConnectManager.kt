package com.example.diaviseo.healthconnect

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthConnectManager private constructor(
    private val healthConnectClient: HealthConnectClient
) {

    companion object {
        fun createIfAvailable(context: Context): HealthConnectManager? {
            return try {
                val client = HealthConnectClient.getOrCreate(context)
                HealthConnectManager(client)
            } catch (e: Exception) {
                Log.e("HealthConnect", "HealthConnectClient 생성 실패: ${e.message}")
                null
            }
        }

        fun isAvailable(context: Context): Boolean {
            return try {
                context.packageManager.getPackageInfo("com.google.android.apps.healthdata", 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
//        HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY
    )

    fun getPermissions(): Set<String> = permissions

    suspend fun hasAllPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return permissions.all { it in granted }
    }

    private fun allTimeRange(): TimeRangeFilter {
        return TimeRangeFilter.before(Instant.now()) // 시작 시점 제한 없음
    }


    private fun recentMonthRange(): TimeRangeFilter = TimeRangeFilter.between(
        Instant.now().minus(30, ChronoUnit.DAYS),
        Instant.now()
    )

    suspend fun readSteps(): List<StepsRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(StepsRecord::class, recentMonthRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readSteps error: ${e.message}")
            emptyList()
        }
    }

    suspend fun readExerciseSessions(): List<ExerciseSessionRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(ExerciseSessionRecord::class, recentMonthRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readExerciseSessions error: ${e.message}")
            emptyList()
        }
    }

    // 범위 지정 버전 추가 (WorkManager 등에서 사용 가능)
    suspend fun readExerciseSessions(
        start: ZonedDateTime,
        end: ZonedDateTime
    ): List<ExerciseSessionRecord> =
        withContext(Dispatchers.IO) {
            try {
                val request = ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start.toInstant(), end.toInstant())
                )
                healthConnectClient.readRecords(request).records
            } catch (e: Exception) {
                Log.e("HealthConnect", "readExerciseSessions(start, end) error: ${e.message}")
                emptyList()
            }
        }


    suspend fun readSteps(start: ZonedDateTime, end: ZonedDateTime): List<StepsRecord> =
        withContext(Dispatchers.IO) {
            try {
                val request = ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start.toInstant(), end.toInstant())
                )
                healthConnectClient.readRecords(request).records
            } catch (e: Exception) {
                Log.e("HealthConnect", "readSteps(start, end) error: ${e.message}")
                emptyList()
            }
        }
}
