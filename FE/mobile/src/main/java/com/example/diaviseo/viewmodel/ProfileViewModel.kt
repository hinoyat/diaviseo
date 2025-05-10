package com.example.diaviseo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class ProfileViewModel : ViewModel() {

    private val _myProfile = MutableStateFlow<FetchProfileResponse?>(null)
    val myProfile: StateFlow<FetchProfileResponse?> = _myProfile

    // 나중에 키 몸무게 바뀔 때마다 여기 bmr, recommendedEat, recommendedFit 다 바꿔줘야함
    private val _bmr = MutableStateFlow(0.0)
    val bmr: StateFlow<Double> = _bmr

    private val _recommendedEat = MutableStateFlow(0)
    val recommendedEat: StateFlow<Int> = _recommendedEat

    private val _recommendedFit = MutableStateFlow(0)
    val recommendedFit: StateFlow<Int> = _recommendedFit

    fun calculateAge(birth: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birth, formatter)
        val today = LocalDate.now()
        return Period.between(birthDate, today).years
    }

    fun fetchMyProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userApiService.fetchMyProfile()
                if (response.status == "OK") {
                    _myProfile.value = response.data  // ✅ 성공 시 상태 저장

                    val profile = _myProfile.value
                    val age = calculateAge(profile?.birthday.toString())

                    profile?.weight?.let { weight ->
                        profile.height?.let { height ->
                            val calBmr = if (profile.gender == "M") {
                                (10 * weight) + (6.25 * height) - (5 * age) + 5
                            } else {
                                (10 * weight) + (6.25 * height) - (5 * age) - 161
                            }
                            _bmr.value = calBmr

                            _recommendedEat.value = when (profile.goal) {
                                "WEIGHT_LOSS" -> (_bmr.value * 1.4).roundToInt() - 500
                                "WEIGHT_GAIN" -> (_bmr.value * 1.4).roundToInt() + 300
                                else -> (_bmr.value * 1.4).roundToInt()
                            }

                            _recommendedFit.value = when (profile.goal) {
                                "WEIGHT_LOSS" -> (_bmr.value * 1.4 * 0.15).roundToInt() + 150
                                "WEIGHT_GAIN" -> (_bmr.value * 1.4 * 0.15).roundToInt() + 70
                                else -> (_bmr.value * 1.4 * 0.15).roundToInt()
                            }
                        }
                    }

                } else {
                    Log.e("Profile", "불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "예외 발생: ${e.message}")
            }
        }
    }
}