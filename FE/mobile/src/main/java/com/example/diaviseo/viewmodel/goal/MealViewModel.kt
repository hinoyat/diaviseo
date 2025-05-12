package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel: ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _dailyNutrition = MutableStateFlow<DailyNutritionResponse?>(null)
//    private val _dailyNutrition = MutableStateFlow<DailyNutritionResponse?>(
//        DailyNutritionResponse(date = "2025-05-07",
//            totalCalorie = 708,
//            totalCarbohydrate = 384.00,
//            totalProtein = 280.40,
//            totalFat = 42.00,
//            totalSugar = 1.80,
//            totalSodium = 590.00)
//    )
    val dailyNutrition: StateFlow<DailyNutritionResponse?> = _dailyNutrition


    fun fetchDailyNutrition(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.mealApiService.fetchDailyNutrition(date = date)
                if (response.status == "OK") {
                    _dailyNutrition.value = response.data
                    _isLoading.value = false
                } else {
                    Log.e("MealViewModel", "일일 영양소 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "일일 영양소 예외 발생: ${e.message}")
            }
        }
    }
}