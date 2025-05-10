package com.example.diaviseo.healthconnect.processor

import androidx.health.connect.client.records.StepsRecord
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object StepDataProcessor {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    // 걸음수 데이터를 날짜별로 정리하여 서버 전송 형태로 변환

    fun process(steps: List<StepsRecord>):List<Map<String, Any>> {
        return steps.map {
            val localDate = it.startTime
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(dateFormatter)

            mapOf(
                "stepCount" to it.count,
                "date" to localDate
            )
        }
    }
}