package com.example.diaviseo.viewmodel.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import com.example.diaviseo.network.body.dto.req.BodyUpdateRequest
import com.example.diaviseo.network.body.dto.res.BodyInfoResponse
import com.example.diaviseo.network.body.dto.res.MonthlyAverageBodyInfoResponse
import com.example.diaviseo.network.body.dto.res.OcrBodyResultResponse
import com.example.diaviseo.network.body.dto.res.WeeklyAverageBodyInfoResponse
import com.example.diaviseo.network.user.dto.res.UserPhysicalInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeightViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _bodyInfo = MutableStateFlow<BodyInfoResponse?>(null)
    val bodyInfo: StateFlow<BodyInfoResponse?> = _bodyInfo

    private val _bodyLatestInfo = MutableStateFlow<BodyInfoResponse?>(null)
    val bodyLatestInfo: StateFlow<BodyInfoResponse?> = _bodyLatestInfo

    private val _physicalInfo = MutableStateFlow<UserPhysicalInfoResponse?>(null)
    val physicalInfo: StateFlow<UserPhysicalInfoResponse?> = _physicalInfo

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

    fun loadBodyData(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.bodyApiService.loadBodyData(date)
                if (response.status == "OK") {
                    _bodyInfo.value = response.data!!
                } else if (response.status == "NOT_FOUND") {
                    // CODE 404
                    _bodyInfo.value = null // data : null
                }

                _isLoading.value = false
            } catch (e: Exception) {
                when {
                    // Retrofit 의 HTTP 에러를 HttpException 으로 던져줄 때
                    e is HttpException && e.code() == 404 -> {
                        // 여기서 404 전용 로직 처리
                        _bodyInfo.value = null
                        Log.d("WeightViewModel", "404 에러 발생해서 데이터는 null 로 세팅됨")
                    }

                    else -> {
                        // 그 외 예외 처리
                        Log.e("WeightViewModel", "체성분 조회 실패: ${e.message}")
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLatestBodyData(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.bodyApiService.loadLatestBodyData(date).data
                if (response != null) {
                    _bodyLatestInfo.value = response[0]
                }

                _isLoading.value = false
            } catch (e: Exception) {
                when {
                    // Retrofit 의 HTTP 에러를 HttpException 으로 던져줄 때
                    e is HttpException && e.code() == 404 -> {
                        // 여기서 404 전용 로직 처리
                        _bodyLatestInfo.value = null
                        Log.d("WeightViewModel", "404 에러 발생해서 데이터는 null 로 세팅됨")
                    }

                    else -> {
                        // 그 외 예외 처리
                        Log.e("WeightViewModel", "최신 체성분 조회 실패: ${e.message}")
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPhysicalInfo(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.userApiService.fetchPhysicalInfo(date = date)
                if (response.status == "OK") {
                    _physicalInfo.value = response.data

                    _isLoading.value = false
                } else {
                    Log.e("WeightViewModel", "일일 신체 칼로리 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "일일 신체 칼로리 예외 발생: ${e.message}")
            }
        }
    }

    fun updateBodyInfo(
        bodyId: Int,
        request: BodyUpdateRequest
        ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.bodyApiService.updateBodyInfo(request = request, bodyId = bodyId)
                if (response.status == "OK") {
                    _bodyLatestInfo.value = response.data

                    _isLoading.value = false
                } else {
                    Log.e("WeightViewModel", "신체 정보 업데이트 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "신체 정보 업데이트 예외 발생: ${e.message}")
            }
        }
    }

    fun registerBodyData(request: BodyRegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.bodyApiService.registerBodyData(request = request)
                if (response.status == "OK") {
                    _bodyLatestInfo.value = response.data

                    _isLoading.value = false
                } else {
                    Log.e("WeightViewModel", "신체 정보 첫 등록 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "신체 정보 첫 등록 발생: ${e.message}")
            }
        }
    }
}