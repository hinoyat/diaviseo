package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.meal.dto.res.DailyNutritionResponse
import com.example.diaviseo.network.meal.dto.res.NutritionStatsEntry
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import com.example.diaviseo.network.user.dto.res.UserPhysicalInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

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

    private val _nowPhysicalInfo = MutableStateFlow<UserPhysicalInfoResponse?>(null)
    val nowPhysicalInfo: StateFlow<UserPhysicalInfoResponse?> = _nowPhysicalInfo

    private val _mealStatistic = MutableStateFlow<List<NutritionStatsEntry>?>(null)
    val mealStatistic: StateFlow<List<NutritionStatsEntry>?> = _mealStatistic

    private val _carbRatio =  MutableStateFlow(0.00)
    val carbRatio: StateFlow<Double> = _carbRatio

    private val _sugarRatio =  MutableStateFlow(0.00)
    val sugarRatio: StateFlow<Double> = _sugarRatio

    private val _proteinRatio =  MutableStateFlow(0.00)
    val proteinRatio: StateFlow<Double> = _proteinRatio

    private val _fatRatio =  MutableStateFlow(0.00)
    val fatRatio: StateFlow<Double> = _fatRatio

    private val _totalCalorie =  MutableStateFlow(0)
    val totalCalorie: StateFlow<Int> = _totalCalorie

    // 탄단지당 비율 (먹은 칼로리, 권장 칼로리, 소수점 자리수)
    fun calculateRatio(nutrient: Double?, calorie: Int?, scale: Int = 2, n: Int): Double {
        if (calorie == null || calorie == 0) return 0.0
        val numerator = BigDecimal.valueOf(nutrient ?: 0.0).multiply(BigDecimal.valueOf(n.toDouble()))
        val denominator = BigDecimal.valueOf(calorie.toDouble())
        return numerator.divide(denominator, scale, RoundingMode.HALF_UP).toDouble()
    }

    fun fetchDailyNutrition(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.mealApiService.fetchDailyNutrition(date = date)
                if (response.status == "OK") {
                    _dailyNutrition.value = response.data

                    // 넘겨줄 total 칼로리 결정 (1285는 임시값)
                    _totalCalorie.value = response.data?.totalCalorie?.let {
                        if (it > _nowPhysicalInfo.value?.recommendedIntake!!) {
                            response.data.totalCalorie
                        } else {
                            _nowPhysicalInfo.value?.recommendedIntake
                        }
                    }!!

                    _carbRatio.value = calculateRatio(_dailyNutrition.value?.totalCarbohydrate, _totalCalorie.value, n = 4)
                    _sugarRatio.value = calculateRatio(_dailyNutrition.value?.totalSugar, _totalCalorie.value, n = 4)
                    _proteinRatio.value = calculateRatio(_dailyNutrition.value?.totalProtein, _totalCalorie.value, n = 4)
                    _fatRatio.value = calculateRatio(_dailyNutrition.value?.totalFat, _totalCalorie.value, n = 9)


                    _isLoading.value = false
                } else {
                    Log.e("MealViewModel", "일일 영양소 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "일일 영양소 예외 발생: ${e.message}")
            }
        }
    }

    fun fetchPhysicalInfo(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.userApiService.fetchPhysicalInfo(date = date)
                if (response.status == "OK") {
                    _nowPhysicalInfo.value = response.data

                    _isLoading.value = false
                } else {
                    Log.e("MealViewModel", "일일 신체 칼로리 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "일일 신체 칼로리 예외 발생: ${e.message}")
            }
        }
    }

    fun fetchMealStatistic(periodType: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.mealApiService.fetchMealStatistic(periodType = periodType ,endDate = endDate)
                if (response.status == "OK") {
                    _mealStatistic.value = response.data?.data
                    Log.d("MealViewModel", "식단통계 잘 들어감?!?!?!?!? ${_mealStatistic.value}")

                    _isLoading.value = false
                } else {
                    Log.e("MealViewModel", "식단 통계 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "식단 통계 칼로리 예외 발생: ${e.message}")
            }
        }
    }
}