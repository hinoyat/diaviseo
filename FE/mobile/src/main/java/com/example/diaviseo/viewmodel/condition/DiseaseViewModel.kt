package com.example.diaviseo.viewmodel.condition

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.condition.dto.res.DiseaseResponse
import kotlinx.coroutines.launch

class DiseaseViewModel : ViewModel() {

    var diseaseList by mutableStateOf<List<DiseaseResponse>>(emptyList())
        private set

    var userDiseaseSet by mutableStateOf<Set<Long>>(emptySet())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var initialUserDiseaseSet = emptySet<Long>()
        private set

    // 전체 + 유저 질환 데이터 로딩
    fun loadDiseaseData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val allResponse = RetrofitInstance.conditionApiService.getAllDiseases()
                val userResponse = RetrofitInstance.conditionApiService.getUserDiseases()

                diseaseList = allResponse.data ?: emptyList()
                userDiseaseSet = userResponse.data?.map { it.diseaseId }?.toSet() ?: emptySet()
                initialUserDiseaseSet = userDiseaseSet.toSet()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // 토글 요청
    fun toggleDisease(diseaseId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.conditionApiService.toggleDisease(diseaseId)
                val isNowRegistered = response.data?.isRegistered ?: false

                userDiseaseSet = if (isNowRegistered) {
                    userDiseaseSet + diseaseId
                } else {
                    userDiseaseSet - diseaseId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 선택 여부 확인
    fun isSelected(diseaseId: Long): Boolean {
        return diseaseId in userDiseaseSet
    }

    val hasChanges: Boolean
        get() = userDiseaseSet != initialUserDiseaseSet

    fun commitChanges() {
        initialUserDiseaseSet = userDiseaseSet.toSet()
    }

    fun revertChanges() {
        userDiseaseSet = initialUserDiseaseSet.toSet()
    }
}
