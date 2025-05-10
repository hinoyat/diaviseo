package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(): ViewModel() {
    private val _todayEat = MutableStateFlow(0)
    val todayEat: StateFlow<Int> = _todayEat

    private val _todayFit = MutableStateFlow(0)
    val todayFit: StateFlow<Int> = _todayFit
}