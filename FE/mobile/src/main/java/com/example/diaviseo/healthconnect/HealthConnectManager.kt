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
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {

    // ✅ Health Connect 클라이언트 객체 생성
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    // ✅ 필요한 READ 권한 목록 정의
    private val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    // ✅ 외부에서 권한 목록 접근 가능하도록 getter 제공
    fun getPermissions(): Set<String> = permissions

    // ✅ 현재 권한 상태 확인 (모든 권한이 허용됐는지 여부 반환)
    suspend fun hasAllPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return permissions.all { it in granted }
    }

    // ✅ 전체 데이터 조회용 기간 필터 (전체 기간 조회시 사용할 예정)
    private fun allTimeRange(): TimeRangeFilter {
        return TimeRangeFilter.before(Instant.now())
    }

    // ✅ 최근 7일 기준 필터
    private fun recentWeekRange(): TimeRangeFilter {
        return TimeRangeFilter.between(
            Instant.now().minus(7, ChronoUnit.DAYS),
            Instant.now()
        )
    }

    // ✅ 걸음 수 데이터 조회
    suspend fun readSteps(): List<StepsRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(StepsRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readSteps error: ${e.message}")
            emptyList()
        }
    }
    // ✅ 심박수 데이터 조회
    suspend fun readHeartRates(): List<HeartRateRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(HeartRateRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readHeartRates error: ${e.message}")
            emptyList()
        }
    }
    // ✅ 총 소모 칼로리 조회
    suspend fun readTotalCalories(): List<TotalCaloriesBurnedRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(TotalCaloriesBurnedRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readTotalCalories error: ${e.message}")
            emptyList()
        }
    }
    // ✅ 활동 칼로리 조회
    suspend fun readActiveCalories(): List<ActiveCaloriesBurnedRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readActiveCalories error: ${e.message}")
            emptyList()
        }
    }
    // ✅ 이동 거리 조회
    suspend fun readDistance(): List<DistanceRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(DistanceRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readDistance error: ${e.message}")
            emptyList()
        }
    }
    // ✅ 운동 세션 조회 (예: 걷기, 달리기, 자전거 등)
    suspend fun readExerciseSessions(): List<ExerciseSessionRecord> = withContext(Dispatchers.IO) {
        try {
            val request = ReadRecordsRequest(ExerciseSessionRecord::class, recentWeekRange())
            healthConnectClient.readRecords(request).records
        } catch (e: Exception) {
            Log.e("HealthConnect", "readExerciseSessions error: ${e.message}")
            emptyList()
        }
    }
    // ✅ Health Connect 앱이 설치되어 있는지 확인
    fun isAvailable(): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo("com.google.android.apps.healthdata", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    // ✅ 전체 건강 데이터를 로그로 출력 (테스트/디버깅용)
    suspend fun logAllHealthData() = withContext(Dispatchers.IO) {
        try {
            // 걸음 수
            val steps = readSteps()
            steps.forEach {
                Log.d("HealthConnect", "🚶 Steps: ${it.startTime} ~ ${it.endTime} ➜ ${it.count}보")
            }

            // 심박수
//            val heartRates = readHeartRates()
//            heartRates.forEach {
//                Log.d("HealthConnect", "❤️ HeartRate: ${it.time} ➜ ${it.samples.joinToString { s -> "${s.time}:${s.beatsPerMinute}bpm" }}")
//            }

            // 총 소모 칼로리
            val totalCalories = readTotalCalories()
            totalCalories.forEach {
                Log.d("HealthConnect", "🔥 TotalCalories: ${it.startTime} ~ ${it.endTime} ➜ ${it.energy.inKilocalories} kcal")
            }

            // 활동 칼로리
            val activeCalories = readActiveCalories()
            activeCalories.forEach {
                Log.d("HealthConnect", "🏃 ActiveCalories: ${it.startTime} ~ ${it.endTime} ➜ ${it.energy.inKilocalories} kcal")
            }

            // 이동 거리
            val distances = readDistance()
            distances.forEach {
                Log.d("HealthConnect", "📏 Distance: ${it.startTime} ~ ${it.endTime} ➜ ${it.distance.inKilometers} km")
            }

            // 운동 세션
            val sessions = readExerciseSessions()
            sessions.forEach {
                Log.d("HealthConnect", "🧘 Exercise: ${it.startTime} ~ ${it.endTime} ➜ ${it.exerciseType}")
            }

        } catch (e: Exception) {
            Log.e("HealthConnect", "💥 데이터 로깅 중 오류 발생: ${e.message}")
        }
    }

    // ✅ StepsRecord 객체 전체를 raw 로그로 출력 (구조 확인용)
    suspend fun logRawSteps() = withContext(Dispatchers.IO) {
        val steps = readSteps()
        steps.forEach {
            Log.d("HealthConnect", it.toString())
        }
    }

}
