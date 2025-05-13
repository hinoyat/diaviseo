package com.example.diaviseo.viewmodel.register.body

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate


class BodyRegisterViewModel: ViewModel() {

    // 상태값들 (입력값)
    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _bodyFat = MutableStateFlow("")
    val bodyFat: StateFlow<String> = _bodyFat

    private val _muscleMass = MutableStateFlow("")
    val muscleMass: StateFlow<String> = _muscleMass


    // 입력값 업데이트 함수
    fun onWeightChange(value: String){
        _weight.value = value
    }

    fun onBodyFatChange(value:String){
        _bodyFat.value = value
    }

    fun onMuscleMassChange(value:String){
        _muscleMass.value = value
    }

    // 유효성 검사
    fun isInputValid(): Boolean {
        return weight.value.isNotBlank() &&
                bodyFat.value.isNotBlank() &&
                muscleMass.value.isNotBlank()
    }
    
    // 초기화 함수
    fun resetInput() {
        _weight.value = ""
        _bodyFat.value = ""
        _muscleMass.value = ""
    }

    // 등록 API 호출
    fun registerBodyData (
        onSuccess:() -> Unit,
        onError: (String) -> Unit,
    ){
        viewModelScope.launch {
            try{
                val request = BodyRegisterRequest(
                    weight = weight.value.toDouble(),
                    bodyFat = bodyFat.value.toDouble(),
                    muscleMass = muscleMass.value.toDouble(),
                    measurementDate = LocalDate.now().toString()
                )
                val response = RetrofitInstance.bodyApiService.registerBodyData(request)

                if (response.status == "OK" || response.status == "CREATED") {
                    onSuccess()
                } else {
                    onError("등록 실패 : ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("BodyRegister", "등록 중 오류: ${e.message}",e)
                onError("에러 발생: ${e.localizedMessage}")
            }
        }
    }
}