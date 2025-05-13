package com.example.diaviseo.viewmodel.register.body

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.body.dto.req.BodyRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class BodyRegisterViewModel : ViewModel() {

    // 상태값들 (입력값)
    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _bodyFat = MutableStateFlow("")
    val bodyFat: StateFlow<String> = _bodyFat

    private val _muscleMass = MutableStateFlow("")
    val muscleMass: StateFlow<String> = _muscleMass

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _measurementDate = MutableStateFlow("")
    val measurementDate: StateFlow<String> = _measurementDate

    // 연, 월, 일 입력값
    val year = MutableStateFlow("")
    val month = MutableStateFlow("")
    val day = MutableStateFlow("")

    // 입력값 업데이트 함수
    fun onWeightChange(value: String) {
        _weight.value = value
    }

    fun onBodyFatChange(value: String) {
        _bodyFat.value = value
    }

    fun onMuscleMassChange(value: String) {
        _muscleMass.value = value
    }

    fun onHeightChange(value: String) {
        _height.value = value
    }

    fun onYearChange(value: String) {
        if (value.length <= 4 && value.all { it.isDigit() }) {
            year.value = value
            updateMeasurementDate()
        }
    }

    fun onMonthChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            month.value = value
            updateMeasurementDate()
        }
    }

    fun onDayChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            day.value = value
            updateMeasurementDate()
        }
    }

    private fun updateMeasurementDate() {
        val y = year.value
        val m = month.value.padStart(2, '0')
        val d = day.value.padStart(2, '0')

        _measurementDate.value = if (y.length == 4 && m.length == 2 && d.length == 2) {
            "$y-$m-$d"
        } else {
            ""
        }
    }

    // 유효성 검사
    fun isInputValid(): Boolean {
        return weight.value.isNotBlank() &&
                bodyFat.value.isNotBlank() &&
                muscleMass.value.isNotBlank() &&
                height.value.isNotBlank() &&
                measurementDate.value.isNotBlank()
    }

    // 초기화 함수
    fun resetInput() {
        _weight.value = ""
        _bodyFat.value = ""
        _muscleMass.value = ""
        _height.value = ""
        _measurementDate.value = ""
        year.value = ""
        month.value = ""
        day.value = ""
    }

    // 등록 API 호출
    fun registerBodyData(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val request = BodyRegisterRequest(
                    weight = weight.value.toDouble(),
                    bodyFat = bodyFat.value.toDouble(),
                    muscleMass = muscleMass.value.toDouble(),
                    height = height.value.toDouble(),
                    measurementDate = measurementDate.value
                )
                val response = RetrofitInstance.bodyApiService.registerBodyData(request)

                if (response.status == "OK" || response.status == "CREATED") {
                    onSuccess()
                } else {
                    onError("등록 실패 : ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("BodyRegister", "등록 중 오류: ${e.message}", e)
                onError("에러 발생: ${e.localizedMessage}")
            }
        }
    }

    // 인바디 OCR
    fun sendOcrRequest(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.bodyApiService.uploadBodyOcrImage(imagePart)
                if (response.status == "OK" || response.status == "CREATED") {
                    response.data?.let { data ->
                        // 입력값 필드에 바로 반영
                        _weight.value = data.weight.toString()
                        _bodyFat.value = data.bodyFat.toString()
                        _muscleMass.value = data.muscleMass.toString()
                        _height.value = data.height.toString()

                        // 날짜도 분리해서 상태값에 반영
                        val parts = data.measurementDate.split("-")
                        if (parts.size == 3) {
                            year.value = parts[0]
                            month.value = parts[1]
                            day.value = parts[2]
                            updateMeasurementDate()
                        }
                    }
                } else {
                    Log.e("OCR", "서버 응답 오류: ${response.message}")
                }
            } catch (e: Exception) {

                Log.e("OCR", "OCR 실패: ${e.message}", e)
            }
        }
    }

}
