package com.example.diaviseo.util

enum class GoalType {
    LOSS, MAINTAIN, GAIN
}

data class FinalGuideInfo(
    val bmr: Int,                // 기초대사량
    val actualIntake: Int,         // ✅ 실제 체중 기반
    val idealIntake: Int,          // ✅ 표준 체중 기반
    val deficit: Int,            // 권장섭취 - BMR
    val standardWeight: Float,   // 표준 체중
    val goalType: GoalType       // 목표 유형 (감량/유지/증량)
)

object FinalGuideMessageBuilder {

    fun calculateBMR(gender: String, weightKg: Float, heightCm: Float, age: Int): Int {
        return if (gender == "남") {
            (66.47 + (13.75 * weightKg) + (5 * heightCm) - (6.76 * age)).toInt()
        } else {
            (655.1 + (9.56 * weightKg) + (1.85 * heightCm) - (4.68 * age)).toInt()
        }
    }

    fun calculateStandardWeight(gender: String, heightCm: Float): Float {
        return if (gender == "남") {
            (heightCm * heightCm * 22) / 10000f
        } else {
            (heightCm * heightCm * 21) / 10000f
        }
    }

    fun calculateRecommendedIntakeKcal(gender: String, heightCm: Float): Int {
        val standardWeight = calculateStandardWeight(gender, heightCm)
        return (standardWeight * 30).toInt()
    }

    fun getGuideInfo(
        gender: String,
        heightCm: Float,
        weightKg: Float,
        age: Int,
        goalType: GoalType
    ): FinalGuideInfo {
        val bmr = calculateBMR(gender, weightKg, heightCm, age)
        val standardWeight = calculateStandardWeight(gender, heightCm)

        val idealIntake = (standardWeight * 30).toInt()       // 표준 체중 기준
        val actualIntake = (weightKg * 30).toInt()            // 실제 체중 기준
        val deficit = maxOf(actualIntake - bmr, 0)

        return FinalGuideInfo(bmr, actualIntake, idealIntake, deficit, standardWeight, goalType)
    }
}
