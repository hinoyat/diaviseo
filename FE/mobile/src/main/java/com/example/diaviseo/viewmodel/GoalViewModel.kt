package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.*
import java.time.LocalDate
import android.util.Log


class GoalViewModel : ViewModel() {
    private val _goal = MutableStateFlow("") // "감량", "유지", "증량"
    val goal: StateFlow<String> = _goal

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // TODO: API 호출 대체
            _selectedDate.value = date
            _isLoading.value = false
            Log.d("DatePicker", "넘겨받은 data?????? $date")
        }
    }

    fun setGoal(goal: String) {
        _goal.value = goal
    }

    fun setShowDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
        Log.d("DatePicker", "모달 yes나 no일텐데")
    }
}