package com.example.diaviseo.healthconnect

import android.content.Context
import android.util.Log

object HealthConnectLogger {

    suspend fun logRawSteps(context: Context) {
        val manager = HealthConnectManager.createIfAvailable(context) ?: return
        val steps = manager.readSteps()
        steps.forEach {
            Log.d("HealthConnect", """
                🚶 [Steps]
                - Count: ${it.count}
                - Start: ${it.startTime}
                - End: ${it.endTime}
                - Metadata: ${it.metadata}
            """.trimIndent())
        }
    }

    suspend fun logRawExerciseSessions(context: Context) {
        val manager = HealthConnectManager.createIfAvailable(context) ?: return
        val sessions = manager.readExerciseSessions()
        sessions.forEach {
            Log.d("HealthConnect", """
                🏃 [ExerciseSession]
                - Type: ${it.exerciseType}
                - Title: ${it.title}
                - Start: ${it.startTime}
                - End: ${it.endTime}
                - Notes: ${it.notes}
                - Metadata: ${it.metadata}
            """.trimIndent())
        }
    }
}
