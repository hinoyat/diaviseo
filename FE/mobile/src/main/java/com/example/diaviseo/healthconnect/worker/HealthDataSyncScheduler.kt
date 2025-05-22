package com.example.diaviseo.healthconnect.worker

import android.content.Context
import androidx.work.*
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * 매일 밤 11시(23:00)에 HealthConnect 데이터를 서버에 자동 동기화하기 위한 WorkManager 등록 함수.
 * 앱 최초 연동 시 1회 호출하면, 이후 매일 자동으로 동작함.
 */
fun scheduleDailyHealthSync(context: Context){
    // 현재 시간
    val now = ZonedDateTime.now()

    // 오늘 날짜의 23:00 시점
    val target = now.withHour(23).withMinute(0).withSecond(0).withNano(0)

    // 현재 시간이 23시 이전이면 오늘 23시에 예약, 이후면 내일 23시에 예약
    val initialDelay = if (target.isAfter(now)) {
        java.time.Duration.between(now, target)
    } else {
        java.time.Duration.between(now, target.plusDays(1))
    }

    // 네트워크 연결 및 배터리 상태 조건 설정
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // 인터넷 연결 필수
        .setRequiresBatteryNotLow(true)  // 배터리 부족 시 실행 안 함
        .build()

    // 주기성 설정: 매일 1회 실행 + 초기 지연 시간 설정
    val workRequest = PeriodicWorkRequestBuilder<HealthDataSyncWorker>(
        1, TimeUnit.DAYS  // 매일 반복
    )
        .setInitialDelay(initialDelay) // 첫 실행까지 대기 시간
        .setConstraints(constraints) // 실행 조건
        .build()

    // WorkManager에 고유 이름으로 등록
    // 이미 등록되어 있으면 UPDATE 정책으로 새로 덮어씀
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_health_sync", // 고유 이름 : 덮어쓰기 방지
        ExistingPeriodicWorkPolicy.UPDATE, // 이미 있으면 대체
        workRequest
    )
}