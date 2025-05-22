package com.example.diaviseo.healthconnect.processor

import androidx.health.connect.client.records.StepsRecord
import com.example.diaviseo.network.exercise.dto.req.StepRecordRequest
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object StepDataProcessor {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun process(steps: List<StepsRecord>): List<StepRecordRequest> {
        return steps.map {
            val localDate = it.startTime
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(dateFormatter)

            StepRecordRequest(
                stepDate = localDate,
                stepCount = it.count.toInt()
            )
        }
    }
}
