package com.example.diaviseo.viewmodel.condition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.condition.dto.res.FoodAllergyResponse
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class AllergyViewModel : ViewModel() {

    var allergyList by mutableStateOf<List<FoodAllergyResponse>>(emptyList())
        private set

    var userAllergySet by mutableStateOf<Set<Long>>(emptySet())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var initialUserAllergySet = emptySet<Long>()
        private set

    // 전체 + 유저 알러지 로딩
    fun loadAllergyData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val allResponse = RetrofitInstance.conditionApiService.getAllAllergies()
                val userResponse = RetrofitInstance.conditionApiService.getUserAllergies()

                allergyList = allResponse.data ?: emptyList()
                userAllergySet = userResponse.data?.map { it.allergyId }?.toSet() ?: emptySet()
                initialUserAllergySet = userAllergySet.toSet()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }


    // 토글 동작 (서버 + 상태 동기화)
    fun toggleAllergy(allergyId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.conditionApiService.toggleAllergy(allergyId)
                val isNowRegistered = response.data?.isRegistered ?: false

                userAllergySet = if (isNowRegistered) {
                    userAllergySet + allergyId
                } else {
                    userAllergySet - allergyId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 선택 여부 확인
    fun isSelected(allergyId: Long): Boolean {
        return allergyId in userAllergySet
    }

    val hasChanges: Boolean
        get() = userAllergySet != initialUserAllergySet

    fun commitChanges() {
        initialUserAllergySet = userAllergySet.toSet()
    }

    fun revertChanges() {
        userAllergySet = initialUserAllergySet.toSet()
    }

}
