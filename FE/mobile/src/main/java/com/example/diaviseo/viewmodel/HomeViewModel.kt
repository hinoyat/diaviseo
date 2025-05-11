package com.example.diaviseo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _todayEat = MutableStateFlow(0)
    val todayEat: StateFlow<Int> = _todayEat

    private val _todayFit = MutableStateFlow(0)
    val todayFit: StateFlow<Int> = _todayFit

    private val _totalCalorie = MutableStateFlow(10)
    val totalCalorie: StateFlow<Int> = _totalCalorie

    private val _totalExerciseCalorie = MutableStateFlow(30)
    val totalExerciseCalorie: StateFlow<Int> = _totalExerciseCalorie

    fun fetchDailyNutrition(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.mealApiService.fetchDailyNutrition(date = date)
                if (response.status == "OK") {
                    _totalCalorie.value = response.data?.totalCalorie!!
                    _isLoading.value = false
                } else {
                    Log.e("HomeViewModel", "일일 영양소 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "일일 영양소 예외 발생: ${e.message}")
            }
        }
    }

    fun fetchDailyExercise(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.exerciseApiService.fetchDailyExercise(date = date)
                if (response.status == "OK") {
                    _totalExerciseCalorie.value = response.data?.totalCalories!!  // ✅ 성공 시 상태 저장
                    _isLoading.value = false
                } else {
                    Log.e("HomeViewModel", "일일 운동 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "일일 운동 예외 발생: ${e.message}")
            }
        }
    }
}