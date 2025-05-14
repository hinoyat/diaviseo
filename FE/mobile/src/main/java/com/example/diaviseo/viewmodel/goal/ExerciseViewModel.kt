package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.exercise.dto.res.DailyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.DayExerciseStatsResponse.ExerciseDetail
import com.example.diaviseo.network.exercise.dto.res.MonthlyExerciseStatsResponse
import com.example.diaviseo.network.exercise.dto.res.WeeklyExerciseStatsResponse
import com.example.diaviseo.network.meal.dto.res.NutritionStatsEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories: StateFlow<Int> = _totalCalories

    private val _totalExerciseTime = MutableStateFlow(0)
    val totalExerciseTime: StateFlow<Int> = _totalExerciseTime

    private val _exerciseCount = MutableStateFlow(0)
    val exerciseCount: StateFlow<Int> = _exerciseCount

    private val _exerciseList = MutableStateFlow<List<ExerciseDetail>>(listOf())
    val exerciseList: StateFlow<List<ExerciseDetail>> = _exerciseList

    // 운동 통계
    private val _dailyStats = MutableStateFlow<List<DailyExerciseStatsResponse.DailyExercise>?>(null)
    val dailyStats: StateFlow<List<DailyExerciseStatsResponse.DailyExercise>?> = _dailyStats

    private val _weeklyStats = MutableStateFlow<List<WeeklyExerciseStatsResponse.WeeklyExercise>?>(null)
    val weeklyStats: StateFlow<List<WeeklyExerciseStatsResponse.WeeklyExercise>?> = _weeklyStats

    private val _monthlyStats = MutableStateFlow<List<MonthlyExerciseStatsResponse.MonthlyExercise>?>(null)
    val monthlyStats: StateFlow<List<MonthlyExerciseStatsResponse.MonthlyExercise>?> = _monthlyStats

    fun setTotalCalories(calories: Int) {
        _totalCalories.value += calories
    }

    fun setTotalExerciseTime(time: Int) {
        _totalExerciseTime.value += time
    }

    fun fetchDailyExercise(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.exerciseApiService.fetchDailyExercise(date = date)
                if (response.status == "OK") {
                    _totalCalories.value = response.data?.totalCalories!!
                    _totalExerciseTime.value = response.data.totalExerciseTime!!
                    _exerciseCount.value = response.data.exerciseCount!!
                    _exerciseList.value = response.data.exercises

                    _isLoading.value = false
                } else {
                    Log.e("ExerciseViewModel", "일일 운동 기록 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "일일 운동 기록 예외 발생: ${e.message}")
            }
        }
    }

    fun deleteExercise(exerciseId: Int){
        viewModelScope.launch {
            try {
                RetrofitInstance.exerciseApiService.deleteExercise(exerciseId = exerciseId)
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "운동 삭제 예외 발생: ${e.message}")
            }
        }
    }

    fun fetchAllStats(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val daily = RetrofitInstance.exerciseApiService.getDailyExerciseStats(date).data?.dailyExercises
                _dailyStats.value = daily

                val weekly = RetrofitInstance.exerciseApiService.getWeeklyExerciseStats(date).data?.weeklyExercises
                _weeklyStats.value = weekly

                val monthly = RetrofitInstance.exerciseApiService.getMonthlyExerciseStats(date).data?.monthlyExercises
                _monthlyStats.value = monthly

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "운동 통계 조회 예외 발생: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}
