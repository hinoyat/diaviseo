package com.example.diaviseo.healthconnect.processor

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.example.diaviseo.model.exercise.ExerciseData
import com.example.diaviseo.network.exercise.dto.req.HealthSyncExerciseRequest
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ExerciseSessionRecordProcessor {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun toRequestList(records: List<ExerciseSessionRecord>): List<HealthSyncExerciseRequest> {
        return records.mapNotNull { record ->
            val startZoned = record.startTime.atZone(ZoneId.systemDefault())
            val formattedDate = startZoned.toLocalDateTime().format(dateTimeFormatter)
            val durationMinutes = Duration.between(record.startTime, record.endTime).toMinutes().toInt()

            val exerciseType = record.exerciseType
            val exerciseInfo = ExerciseData.exerciseList.find { it.id == exerciseType }

            if (exerciseInfo == null) {
                // 🔴 매핑되지 않은 운동은 무시 (혹은 Log 찍어서 테스트 예정)
                null
            } else {
                val uuid = record.metadata.id?.takeIf { it.isNotBlank() }
                val totalCalorie = durationMinutes * exerciseInfo.calorie
                HealthSyncExerciseRequest(
                    exerciseNumber = exerciseType,
                    exerciseDate = formattedDate,
                    exerciseTime = durationMinutes,
                    exerciseCalorie = totalCalorie,
                    healthConnectUuid = uuid
                )
            }
        }
    }
}
