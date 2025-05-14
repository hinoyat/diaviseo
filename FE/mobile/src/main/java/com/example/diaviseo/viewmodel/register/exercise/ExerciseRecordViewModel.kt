package com.example.diaviseo.viewmodel.register.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExerciseRecordViewModel : ViewModel(){
    // 선택된 운동 정보
    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise = _selectedExercise.asStateFlow()

    // 입력된 운동 시간 (분)
    private val _exerciseTime = MutableStateFlow(30)
    val exerciseTime = _exerciseTime.asStateFlow()

    // 시작 시간 (예: "08:30")
    private val _startTime = MutableStateFlow<String?>(null)
    val startTime = _startTime.asStateFlow()

    // 등록 날짜
    private val _registerDate = MutableStateFlow(LocalDate.now().toString())
    val registerDate = _registerDate.asStateFlow()

    fun setExercise(exercise: Exercise) {
        _selectedExercise.value = exercise
    }

    fun setExerciseTime(time: Int) {
        _exerciseTime.value = time
    }

    fun setStartTime(hour: String, minute: String) {
        if (hour.isNotBlank() && minute.isNotBlank()) {
            _startTime.value = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
        }
    }

    fun setRegisterDate(date: String){
        _registerDate.value = date
    }

    private fun toExerciseRecordRequest(): ExerciseRecordRequest? {
        val exercise = _selectedExercise.value ?: return null
        val time = _exerciseTime.value
        val startTime = _startTime.value

        val formattedDate = startTime?.let {
            val now = LocalDate.now()
            _registerDate.value + "T" + it.padStart(5, '0') // ex. 08:03 → 08:03
        }


        return ExerciseRecordRequest(
            exerciseNumber = exercise.id,
            exerciseDate = formattedDate,
            exerciseTime = time
        )
    }

    fun submitExercise(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val request = toExerciseRecordRequest() ?: return
        Log.d("ExerciseSubmit", "submitExercise 진입")
        Log.d("ExerciseSubmit", "보내는 데이터: $request")
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.exerciseApiService.registerExercise(request)
                Log.d("ExerciseSubmit", "서버 응답: $response")
                if (response.status == "OK") {
                    onSuccess()
                } else {
                    onError(Exception("서버 오류: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("ExerciseSubmit", "예외 발생", e)
                onError(e)
            }
        }
    }

}