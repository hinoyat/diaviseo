package com.example.diaviseo.healthconnect.processor

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.example.diaviseo.network.ExerciseRecordRequest
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ExerciseSessionRecordProcessor {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun toRequest(record: ExerciseSessionRecord): ExerciseRecordRequest {
        val startZoned = record.startTime.atZone(ZoneId.systemDefault())
        val formattedDate = startZoned.toLocalDateTime().format(dateTimeFormatter)
        val durationMinutes = Duration.between(record.startTime, record.endTime).toMinutes().toInt()

        return ExerciseRecordRequest(
            exerciseTypeId = record.exerciseType,
            exerciseDate = formattedDate,
            exerciseTime = durationMinutes,
            exerciseStartTime = formattedDate
        )
    }
}
