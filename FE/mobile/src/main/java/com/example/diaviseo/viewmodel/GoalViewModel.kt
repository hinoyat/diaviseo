package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class GoalViewModel : ViewModel() {
    private val _goal = MutableStateFlow("") // "감량", "유지", "증량"
    val goal: StateFlow<String> = _goal

    fun setGoal(goal: String) {
        _goal.value = goal
    }
}