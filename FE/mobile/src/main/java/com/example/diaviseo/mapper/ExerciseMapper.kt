package com.example.diaviseo.mapper

import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.network.exercise.dto.res.*

fun RecentExerciseResponse.toExercise(): Exercise {
    return Exercise(
        id = exerciseNumber,
        name = exerciseName,
        englishName = exerciseEnglishName,
        category = categoryIdToName(exerciseCategoryId),
        calorie = exerciseCalorie
    )
}

fun FavoriteExerciseResponse.toExercise(): Exercise {
    return Exercise(
        id = exerciseNumber,
        name = exerciseName,
        englishName = "", // 즐겨찾기에는 영어 이름이 없으므로 공란 처리
        category = categoryName,
        calorie = calorie
    )
}

private fun categoryIdToName(id: Int): String = when (id) {
    1 -> "일반"
    2 -> "유산소"
    3 -> "프리 웨이트"
    4 -> "아웃도어"
    5 -> "수상스포츠"
    6 -> "겨울 스포츠"
    7 -> "구기 종목"
    else -> "기타"
}
