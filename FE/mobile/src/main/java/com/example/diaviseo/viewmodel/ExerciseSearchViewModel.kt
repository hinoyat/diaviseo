package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.model.exercise.ExerciseData
import kotlinx.coroutines.flow.*

class ExerciseSearchViewModel : ViewModel() {

    // 🔍 입력 중인 검색어 상태
    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    // 전체 운동 리스트
    private val allExercises: List<Exercise> = ExerciseData.exerciseList

    // 🔎 필터링된 리스트
    val filteredExercises: StateFlow<List<Exercise>> = _keyword
        .debounce(100) // 입력 지연 감지 (optional)
        .mapLatest { input ->
            val trimmed = input.trim()
            if (trimmed.isBlank()) {
                allExercises
            } else {
                allExercises.filter {
                    it.name.contains(trimmed, ignoreCase = true) ||
                            it.category.contains(trimmed, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = allExercises
        )

    // ✏️ 검색어 변경 처리
    fun onKeywordChanged(newKeyword: String) {
        _keyword.value = newKeyword
    }

    // 카테고리별 검색
    fun getExercisesByCategory(category: String?): List<Exercise> {
        return if (category == null) {
            ExerciseData.exerciseList
        } else {
            ExerciseData.exerciseList.filter { it.category == category }
        }
    }

}
