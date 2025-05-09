package com.example.diaviseo.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.FetchProfileResponse
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _myProfile = MutableStateFlow<FetchProfileResponse?>(null)
    val myProfile: StateFlow<FetchProfileResponse?> = _myProfile


    fun fetchMyProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.profileApiService.fetchMyProfile()
                if (response.status == "OK") {
                    _myProfile.value = response.data  // ✅ 성공 시 상태 저장
                } else {
                    Log.e("Profile", "불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "예외 발생: ${e.message}")
            }
        }
    }
}