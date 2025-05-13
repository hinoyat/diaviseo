package com.example.diaviseo.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.user.dto.req.UserUpdateRequest
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel : ViewModel() {
    private val date by mutableStateOf(LocalDate.now().toString())

    private val _myProfile = MutableStateFlow<FetchProfileResponse?>(null)
    val myProfile: StateFlow<FetchProfileResponse?> = _myProfile

    private val _bmr = MutableStateFlow(0.0)
    val bmr: StateFlow<Double> = _bmr

    private val _tdee = MutableStateFlow(0)
    val tdee: StateFlow<Int> = _tdee

    private val _recommendedEat = MutableStateFlow(0)
    val recommendedEat: StateFlow<Int> = _recommendedEat

    private val _recommendedFit = MutableStateFlow(0)
    val recommendedFit: StateFlow<Int> = _recommendedFit

    fun fetchMyProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userApiService.fetchMyProfile()
                if (response.status == "OK") {
                    _myProfile.value = response.data  // ✅ 성공 시 상태 저장

                    try {
                        val kcalResponse = RetrofitInstance.userApiService.fetchPhysicalInfo(date = date)
                        if (kcalResponse.status == "OK") {
                            _recommendedEat.value = kcalResponse.data?.recommendedIntake!!
                            _recommendedFit.value = kcalResponse.data.recommendedExercise
                            _tdee.value = kcalResponse.data.tdee
                            Log.d("Profile", "내 권장섭취량 호출 : ${_recommendedEat.value}")
                            Log.d("Profile", "내 권장소비량 호출 : ${_recommendedFit.value}")
                            Log.d("Profile", "내 tdee 호출 : ${_tdee.value}")

                        } else {
                            Log.e("Profile", "내 권장 섭취량 불러오기 실패: ${kcalResponse.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("Profile", "내 권장 섭취량 예외 발생: ${e.message}")
                    }

                } else {
                    Log.e("Profile", "내 프로필 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "내 프로필 예외 발생: ${e.message}")
            }
        }
    }

    fun updateUserProfile(
        request: UserUpdateRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userApiService.updateMyProfile(request)
                if (response.status == "OK") {
                    _myProfile.value = response.data
                    fetchMyProfile() // BMR 재계산
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }

}