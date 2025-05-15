package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.body.dto.res.MonthlyAverageBodyInfoResponse
import com.example.diaviseo.network.body.dto.res.OcrBodyResultResponse
import com.example.diaviseo.network.body.dto.res.WeeklyAverageBodyInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeightViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _dayList = MutableStateFlow<List<OcrBodyResultResponse>>(emptyList())
    val dayList: StateFlow<List<OcrBodyResultResponse>> = _dayList

    private val _weekList = MutableStateFlow<List<WeeklyAverageBodyInfoResponse>>(emptyList())
    val weekList: StateFlow<List<WeeklyAverageBodyInfoResponse>> = _weekList

    private val _monthList = MutableStateFlow<List<MonthlyAverageBodyInfoResponse>>(emptyList())
    val monthList: StateFlow<List<MonthlyAverageBodyInfoResponse>> = _monthList

    fun fetchAllLists(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val daily = RetrofitInstance.bodyApiService.fetchDailyBodyStatistic(date).data
                if (daily != null) {
                    _dayList.value = daily
                }

                val weekly = RetrofitInstance.bodyApiService.fetchWeeklyBodyStatistic(date).data
                if (weekly != null) {
                    _weekList.value = weekly
                }

                val monthly = RetrofitInstance.bodyApiService.fetchMonthlyBodyStatistic(date).data
                if (monthly != null) {
                    _monthList.value = monthly
                }

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("WeightViewModel", "체성분 통계 조회 예외 발생: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}